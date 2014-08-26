package com.gh.util;

import java.util.Collection;
import java.util.Iterator;

public class BeanUtil extends org.springframework.beans.BeanUtils {
	
	public static <Tt> void copyCollection(final Collection<?> col_Source, Collection<Tt> col_Target, Class<Tt> cls) {
		Iterator<?> ite = col_Source.iterator();
		while (ite.hasNext()) {
			Object object = (Object) ite.next();
			
			Tt tt = null;
			try {
				tt = cls.newInstance();
				copyProperties(object, tt);
				col_Target.add(tt);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}