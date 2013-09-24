/*******************************************************************************
 * Copyright (c) 2013 vogella GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     vogella GmbH - initial API and implementation
 *******************************************************************************/
package com.vogella.e4.di.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

@SuppressWarnings("restriction")
public class OSGiServiceObjectSupplier extends ExtendedObjectSupplier {

	private static final BundleContext CTX;

	static {
		final Bundle bundle = FrameworkUtil.getBundle(OSGiServiceObjectSupplier.class);
		CTX = bundle.getBundleContext();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier#get(org.eclipse.e4.core.di.suppliers.IObjectDescriptor, org.eclipse.e4.core.di.suppliers.IRequestor, boolean, boolean)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object get(final IObjectDescriptor descriptor, final IRequestor requestor, final boolean track, final boolean group) {
		if (descriptor == null) {
			return null;
		}
		// Do not support tracking nor grouping
		if (track || group) {
			throw new IllegalArgumentException("Tracking and grouping not supported");
		}

		final Class<?> descriptorsClass = getDesiredClass(descriptor.getDesiredType());
		if (descriptorsClass == null) {
			return null;
		}

		try {
			final ServiceReference<?>[] allServiceReferences = CTX
					.getAllServiceReferences(descriptorsClass.getName(), null);
			if (allServiceReferences != null) {
				final List result = new ArrayList();
				for (int i = 0; i < allServiceReferences.length; i++) {
					result.add(CTX.getService(allServiceReferences[i]));
				}
				return result;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Class<?> getDesiredClass(Type desiredType) {
		if (desiredType instanceof ParameterizedType) {
			// Type of collection
			final Type collectionType = ((ParameterizedType) desiredType).getRawType();
			//TODO Add support to inject Set, Map, List depending on requestee
			assert collectionType.getClass().equals(List.class);
			
			// Type of elements in collection
			final Type[] elementTypes = ((ParameterizedType) desiredType)
					.getActualTypeArguments();
			for (int i = 0; i < elementTypes.length; i++) {
				// Only care for the first elment type
				final Type elementType = elementTypes[i];
				if (elementType instanceof Class<?>) {
					return (Class<?>) elementType;
				}
			}
		}
		return null;
	}
}
