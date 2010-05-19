package org.drools.repository;

import java.io.File;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;

/**
 * This is a utility to simulate session behavior for the test suite.
 * @author Michael Neale
 *
 */
public class RepositorySessionUtil {

    private static ThreadLocal<RulesRepository> repo = new ThreadLocal<RulesRepository>();
    private static Repository multiThreadedRepository;

//    private static final Logger log = Logger.getLogger( RepositorySessionUtil.class );

    public static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                if (!deleteDir(new File(dir, children[i]))) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }


    public static RulesRepository getRepository() throws RulesRepositoryException {
    	RulesRepository repoInstance = repo.get();
    	//System.out.println("----------getRepository");
        if ( repoInstance == null ) {
        	
        	//System.out.println("----------repoInstance == null");

            File dir = new File( "repository" );
            System.out.println( "DELETING test repo: " + dir.getAbsolutePath() );
            deleteDir( dir );
            System.out.println( "TEST repo was deleted." );

            JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();

            //create a repo instance (startup)
            multiThreadedRepository = config.getJCRRepository(null);

            //create a session
            Session session;
            try {
                session = multiThreadedRepository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
                RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(session);

                //clear out and setup
                if (admin.isRepositoryInitialized()) {
                    admin.clearRulesRepository();
                }
                config.setupRulesRepository( session );
                repoInstance = new RulesRepository( session );

                multiThreadedRepository.login(new SimpleCredentials("ADMINISTRATOR", "password".toCharArray()));
                //loonie hack
                //DroolsRepositoryAccessManager.adminThreadlocal.set(  adminSession );
                repo.set( repoInstance );
            } catch ( Exception e) {
                throw new RulesRepositoryException(e);
            }
        }

        return repoInstance;
    }

   public static synchronized RulesRepository getMultiThreadedRepository() throws RulesRepositoryException {
       if ( multiThreadedRepository == null ) {
                     //System.out.println("----------repoInstance == null");

           File dir = new File( "repository" );
           System.out.println( "DELETING test repo: " + dir.getAbsolutePath() );
           deleteDir( dir );
           System.out.println( "TEST repo was deleted." );

           JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();

           //create a repo instance (startup)
           multiThreadedRepository = config.getJCRRepository(null);

           //create a session to config repo
           Session session;
           try {
               session = multiThreadedRepository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
               RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(session);

               //clear out and setup
               if (admin.isRepositoryInitialized()) {
                   admin.clearRulesRepository();
               }
               config.setupRulesRepository( session );
           } catch ( Exception e) {
               throw new RulesRepositoryException(e);
           }
       }


       //associate this repo instance with thread specific sessions every time.
       Session session;
       try {
           session = multiThreadedRepository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
           RulesRepository threadLocalRepo = new RulesRepository( session );
           return threadLocalRepo;
       } catch (LoginException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       } catch (RepositoryException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
             return null;
   }



}
