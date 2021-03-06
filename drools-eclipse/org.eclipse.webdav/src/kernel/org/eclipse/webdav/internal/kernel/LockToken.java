/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.webdav.internal.kernel;

public class LockToken {

	private String fToken = null;

	public LockToken(String token) {
		fToken = token;
	}

	public String getToken() {
		return fToken;
	}

	public void setToken(String token) {
		fToken = token;
	}

	public String toString() {
		return fToken;
	}
}
