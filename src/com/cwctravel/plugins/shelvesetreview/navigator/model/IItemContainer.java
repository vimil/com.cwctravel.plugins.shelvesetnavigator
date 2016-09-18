package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.List;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

public interface IItemContainer<T, U> {
	public T getItemParent();

	public List<U> getChildren();

	public default boolean hasChildren() {
		return !getChildren().isEmpty();
	}

	public Image getImage();

	public default String getText() {
		return null;
	}

	public default String getDescription() {
		return null;
	}

	public int itemCompareTo(IItemContainer<?, ?> itemContainer);

	public default void decorate(IDecoration decoration) {

	}
}
