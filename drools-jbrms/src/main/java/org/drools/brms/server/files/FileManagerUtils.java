package org.drools.brms.server.files;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.HTMLFileManagerFields;
import org.drools.brms.server.util.ClassicDRLImporter;
import org.drools.brms.server.util.FormData;
import org.drools.brms.server.util.ClassicDRLImporter.Rule;
import org.drools.compiler.DroolsParserException;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;

/**
 * This assists the file manager servlets.
 * @author Fernando Meyer
 */
@Name("fileManager")
@Scope(ScopeType.EVENT)
@AutoCreate
public class FileManagerUtils {

    @In
    public RulesRepository repository;

    /**
     * This attach a file to an asset.
     */
    @Restrict("#{identity.loggedIn}")
    public void attachFile(FormData uploadItem) throws IOException {

        String uuid = uploadItem.getUuid();
        InputStream fileData = uploadItem.getFile().getInputStream();
        String fileName = uploadItem.getFile().getName();

        attachFileToAsset( uuid, fileData, fileName );
        uploadItem.getFile().getInputStream().close();
    }

    /**
     * This utility method attaches a file to an asset.
     */
    @Restrict("#{identity.loggedIn}")
    public void attachFileToAsset(String uuid, InputStream fileData, String fileName) {

        AssetItem item = repository.loadAssetByUUID( uuid );
        item.updateBinaryContentAttachment( fileData );
        item.updateBinaryContentAttachmentFileName( fileName );
        item.checkin( "Attached file: " + fileName );
    }

    /** 
     * The get returns files based on UUID of an asset.
     */
    @Restrict("#{identity.loggedIn}")
    public String loadFileAttachmentByUUID(String uuid, OutputStream out) throws IOException {

        AssetItem item = repository.loadAssetByUUID( uuid );

        byte[] data = item.getBinaryContentAsBytes();

        out.write( data );
        out.flush();

        return item.getBinaryContentAttachmentFileName();
    }

    /**
     * Get the form data from the inbound request.
     */
    public static FormData getFormData(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );

        FormData data = new FormData();
        try {
            List items = upload.parseRequest( request );
            Iterator it = items.iterator();
            while ( it.hasNext() ) {
                FileItem item = (FileItem) it.next();
                if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.FORM_FIELD_UUID ) ) {
                    data.setUuid( item.getString() );
                }
                data.setFile( item );
            }
            return data;
        } catch ( FileUploadException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Load up the approproate package version.
     * @param packageName The name of the package. 
     * @param packageVersion The version (if it is a snapshot).
     * @param isLatest true if the latest package binary will be used (ie NOT a snapshot).
     * @return The filename if its all good.
     */
    public String loadBinaryPackage(String packageName, String packageVersion, boolean isLatest, OutputStream out) throws IOException {
        PackageItem item = null;
        if ( isLatest ) {
            item = repository.loadPackage( packageName );
            byte[] data = item.getCompiledPackageBytes();
            out.write( data );
            out.flush();
            return packageName + ".pkg";
        } else {
            item = repository.loadPackageSnapshot( packageName, packageVersion );
            byte[] data = item.getCompiledPackageBytes();
            out.write( data );
            out.flush();
            return packageName + "_" + URLEncoder.encode( packageVersion, "UTF-8" ) + ".pkg";
        }

    }

    public byte[] exportRulesRepository() {
        try {
            return this.repository.exportRulesRepository();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        } catch ( IOException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    @Restrict("#{identity.loggedIn}")
    public void importRulesRepository(byte[] data) {
        repository.importRulesRepository( data );
    }

    /**
     * This will import DRL from a drl file into a more normalised structure.
     * If the package does not exist, it will be created. 
     * If it does, it will be "merged" in the sense that any new rules in the drl
     * will be created as new assets in the repo, everything else will stay as it was
     * in the repo.
     */
    @Restrict("#{identity.loggedIn}")
    public void importClassicDRL(InputStream drlStream) throws IOException,
                                                       DroolsParserException {

        ClassicDRLImporter imp = new ClassicDRLImporter( drlStream );
        PackageItem pkg = null;
        boolean existing = false;
        if ( repository.containsPackage( imp.getPackageName() ) ) {
            pkg = repository.loadPackage( imp.getPackageName() );
            existing = true;
        } else {
            pkg = repository.createPackage( imp.getPackageName(), "<imported>" );
            pkg.updateHeader( imp.getPackageHeader() );
        }


        for ( Rule rule : imp.getRules() ) {

            if ( existing && pkg.containsAsset( rule.name ) ) {
                //skip it
            } else {

                AssetItem asset = pkg.addAsset( rule.name, "<imported>" );

                if ( imp.isDSLEnabled() ) {
                    asset.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
                } else {
                    asset.updateFormat( AssetFormats.DRL );
                }
                asset.updateContent( rule.content );
                asset.updateExternalSource( "Imported from external DRL" );
            }
        }

        repository.save();
    }

}
