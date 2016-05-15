package com.cwctravel.plugins.shelvesetnavigator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetResourceItem;

public class ShelvesetUtil {
	public static List<ShelvesetResourceItem> groupShelvesetFileItems(ShelvesetItem shelvesetItem,
			List<ShelvesetFileItem> shelvesetFileItems) {
		List<ShelvesetResourceItem> result = new ArrayList<ShelvesetResourceItem>();

		Map<String, Object> root = new HashMap<String, Object>();
		if (shelvesetFileItems != null) {
			for (ShelvesetFileItem shelvesetFileItem : shelvesetFileItems) {
				addFileItemToTree(root, shelvesetFileItem);
			}

			flattenTree(null, null, root);

			traverseTree(shelvesetItem, result, root);
		}

		return result;
	}

	private static void traverseTree(ShelvesetItem shelvesetItem, Object parent, Object child) {
		@SuppressWarnings("unchecked")
		Map<String, Object> childMap = (Map<String, Object>) child;
		ShelvesetFolderItem parentFolder = null;
		if (parent instanceof ShelvesetFolderItem) {
			parentFolder = (ShelvesetFolderItem) parent;
		}
		for (Map.Entry<String, Object> childMapEntry : childMap.entrySet()) {
			String childEntryName = childMapEntry.getKey();
			Object childEntryValue = childMapEntry.getValue();
			if (childEntryValue instanceof ShelvesetFileItem) {
				ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) childEntryValue;
				if (parentFolder != null) {
					ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) parent;
					shelvesetFolderItem.addChild(shelvesetFileItem);
				} else {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) parent;
					list.add(shelvesetFileItem);
				}
			} else {
				ShelvesetFolderItem folder = new ShelvesetFolderItem(shelvesetItem, childEntryName);
				if (parentFolder != null) {
					parentFolder.addChild(folder);
				} else {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) parent;
					list.add(folder);
				}
				traverseTree(shelvesetItem, folder, childEntryValue);
			}
		}
	}

	private static boolean flattenTree(String parentPath, Map<String, Object> parent, Map<String, Object> child) {
		if (parent != null && child.size() == 1) {
			Map.Entry<String, Object> onlyChildEntry = child.entrySet().iterator().next();
			Object onlyChild = onlyChildEntry.getValue();
			if (onlyChild instanceof Map<?, ?>) {
				String path = onlyChildEntry.getKey();
				String newPath = parentPath + "/" + path;
				parent.remove(parentPath);

				parent.put(newPath, onlyChild);
				return true;
			}
			return false;
		} else {
			boolean result = false;
			do {
				result = false;
				Set<Map.Entry<String, Object>> childrenSet = new HashSet<>(child.entrySet());
				for (Map.Entry<String, Object> entry : childrenSet) {
					String path = entry.getKey();
					Object o = entry.getValue();
					if (o instanceof Map<?, ?>) {
						@SuppressWarnings("unchecked")
						Map<String, Object> currentChild = (Map<String, Object>) o;
						if (flattenTree(path, child, currentChild)) {
							result = true;
						}
					}
				}
			} while (result);
			return result;
		}

	}

	@SuppressWarnings("unchecked")
	private static void addFileItemToTree(Map<String, Object> root, ShelvesetFileItem shelvesetFileItem) {
		String path = shelvesetFileItem.getPath();
		String[] pathParts = path.split("/");

		Map<String, Object> current = root;
		for (int i = 0; i < pathParts.length - 1; i++) {
			String pathPart = pathParts[i];
			Object o = current.get(pathPart);
			if (o == null) {
				o = new HashMap<String, Object>();
				current.put(pathPart, o);
			}
			current = (Map<String, Object>) o;
		}

		shelvesetFileItem.setName(pathParts[pathParts.length - 1]);
		current.put(pathParts[pathParts.length - 1], shelvesetFileItem);
	}
}
