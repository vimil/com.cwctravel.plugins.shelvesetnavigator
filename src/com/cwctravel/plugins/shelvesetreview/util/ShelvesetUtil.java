package com.cwctravel.plugins.shelvesetreview.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Status;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.constants.ShelvesetPropertyConstants;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PropertyValue;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

import ms.tfs.versioncontrol.clientservices._03._PropertyValue;
import ms.tfs.versioncontrol.clientservices._03._Shelveset;

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

	public static boolean getPropertyAsBoolean(Shelveset shelveset, String propertyName, boolean defaultValue) {
		boolean result = false;
		String propertyValue = getProperty(shelveset, propertyName, null);
		if (propertyValue != null) {
			result = Boolean.parseBoolean(propertyValue);
		}

		return result;
	}

	public static String[] getPropertyAsStringArray(Shelveset shelveset, String propertyName) {
		String[] result = new String[0];
		String propertyValue = getProperty(shelveset, propertyName, null);
		if (propertyValue != null) {
			result = propertyValue.split(",");
		}
		return result;
	}

	public static String getProperty(Shelveset shelveset, String propertyName, String defaultValue) {
		String result = defaultValue;
		PropertyValue[] propertyValues = shelveset.getPropertyValues();
		if (propertyValues != null) {
			for (PropertyValue propertyValue : propertyValues) {
				if (propertyValue.matchesName(propertyName)) {
					result = (String) propertyValue.getPropertyValue();
				}
			}
		}

		return result;
	}

	public static boolean isShelvesetInactive(Shelveset shelveset) {
		boolean isShelvesetInactive = ShelvesetUtil.getPropertyAsBoolean(shelveset,
				ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG, false);
		String changesetId = ShelvesetUtil.getProperty(shelveset,
				ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);
		return isShelvesetInactive || changesetId != null;
	}

	public static boolean canActivateShelveset(Shelveset shelveset) {
		boolean isShelvesetInactive = ShelvesetUtil.getPropertyAsBoolean(shelveset,
				ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG, false);
		String changesetId = ShelvesetUtil.getProperty(shelveset,
				ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);

		return isShelvesetInactive && changesetId == null;
	}

	public static void markShelvesetInactive(Shelveset shelveset) {
		setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG, Boolean.toString(true));
	}

	public static void markShelvesetActive(Shelveset shelveset) {
		setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG, Boolean.toString(false));
	}

	@SuppressWarnings("restriction")
	public static void setShelvesetProperty(Shelveset shelveset, String property, String value) {
		if (property != null) {
			VersionControlClient versionControlClient = TFSUtil.getVersionControlClient();
			if (versionControlClient != null) {
				Shelveset[] shelvesets = versionControlClient.queryShelvesets(shelveset.getName(),
						shelveset.getOwnerName(), new String[] { property });
				if (shelvesets != null && shelvesets.length == 1) {
					_Shelveset _shelveset = shelveset.getWebServiceObject();
					Shelveset newShelveset = shelvesets[0];
					_Shelveset _newShelveset = newShelveset.getWebServiceObject();
					_PropertyValue[] propertyValuesArray = _newShelveset.getProperties();
					_shelveset.setProperties(propertyValuesArray);

					_PropertyValue newPropertyValue = new _PropertyValue(property, value, null, null);
					_newShelveset.setProperties(new _PropertyValue[] { newPropertyValue });
					versionControlClient.getWebServiceLayer().updateShelveset(shelveset.getName(),
							shelveset.getOwnerName(), newShelveset);

					boolean propertyPresent = false;
					List<_PropertyValue> newPropertyValues = new ArrayList<_PropertyValue>();
					if (propertyValuesArray != null) {
						for (_PropertyValue propertyValue : propertyValuesArray) {
							newPropertyValues.add(propertyValue);
							if (propertyValue.getPname().equals(property)) {
								propertyPresent = true;
								propertyValue.setVal(value);
							}
						}
					}

					if (!propertyPresent) {
						newPropertyValues.add(newPropertyValue);
						_shelveset.setProperties(newPropertyValues.toArray(new _PropertyValue[0]));
					}
				}
			}
		}
	}

	public static boolean deleteShelveset(Shelveset shelveset) {
		boolean result = false;
		if (shelveset != null) {
			VersionControlClient versionControlClient = TFSUtil.getVersionControlClient();
			if (versionControlClient != null) {
				try {
					versionControlClient.deleteShelveset(shelveset.getName(), shelveset.getOwnerName());
					result = true;
				} catch (RuntimeException e) {
					ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
					result = false;
				}
			}
		}
		return result;
	}
}
