/**
 * Copyright 2015 Alex Yanchenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.droidparts.inner.converter;

import java.util.Iterator;
import java.util.Map;

import org.droidparts.inner.ConverterRegistry;
import org.droidparts.inner.ReflectionUtils;
import org.droidparts.inner.TypeHelper;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

public class MapConverter extends Converter<Map<?, ?>> {

	@Override
	public boolean canHandle(Class<?> cls) {
		return TypeHelper.isMap(cls);
	}

	@Override
	public String getDBColumnType() {
		return BLOB;
	}

	@Override
	public <G1, G2> Map<G1, G2> readFromJSON(Class<Map<?, ?>> valType, Class<G1> genericType1, Class<G2> genericType2,
			JSONObject obj, String key) throws Exception {
		JSONObject jo = ConverterRegistry.getConverter(JSONObject.class).readFromJSON(JSONObject.class, genericType1,
				genericType2, obj, key);
		Converter<G1> keyConv = ConverterRegistry.getConverter(genericType1);
		Converter<G2> valConv = ConverterRegistry.getConverter(genericType2);
		Map<G1, G2> map = (Map<G1, G2>) ReflectionUtils.newInstance(valType);
		Iterator<String> it = jo.keys();
		while (it.hasNext()) {
			String ks = it.next();
			G1 k = keyConv.parseFromString(genericType1, null, null, ks);
			G2 v = valConv.readFromJSON(genericType2, null, null, jo, ks);
			map.put(k, v);
		}
		return map;
	}

	@Override
	public <G1, G2> void putToJSON(Class<Map<?, ?>> valType, Class<G1> genericType1, Class<G2> genericType2,
			JSONObject obj, String key, Map<?, ?> val) throws Exception {
		JSONObject o = new JSONObject();
		Converter<G2> valConv = ConverterRegistry.getConverter(genericType2);
		for (Object k : val.keySet()) {
			String ks = k.toString();
			G2 v = (G2) val.get(k);
			valConv.putToJSON(genericType2, null, null, o, ks, v);
		}
		obj.put(key, o);
	}

	@Override
	protected <G1, G2> Map<G1, G2> parseFromString(Class<Map<?, ?>> valType, Class<G1> genericType1,
			Class<G2> genericType2, String str) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public <G1, G2> Map<G1, G2> readFromCursor(Class<Map<?, ?>> valType, Class<G1> genericType1, Class<G2> genericType2,
			Cursor cursor, int columnIndex) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public <G1, G2> void putToContentValues(Class<Map<?, ?>> valueType, Class<G1> genericType1, Class<G2> genericType2,
			ContentValues cv, String key, Map<?, ?> val) throws Exception {
		throw new UnsupportedOperationException();
	}
}
