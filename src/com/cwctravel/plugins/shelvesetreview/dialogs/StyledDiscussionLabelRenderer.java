package com.cwctravel.plugins.shelvesetreview.dialogs;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.internal.DefaultCellRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.util.DateUtil;

@SuppressWarnings("restriction")
public class StyledDiscussionLabelRenderer extends DefaultCellRenderer {
	private Display display;
	private Styler boldStyler;
	private Styler italicStyler;
	private Styler plainStyler;
	private Font boldFont;
	private Font plainFont;
	private Font italicFont;

	public StyledDiscussionLabelRenderer() {
		display = Display.getCurrent();
		plainFont = display.getSystemFont();
		FontData[] boldFontData = getModifiedFontData(plainFont.getFontData(), SWT.BOLD);
		boldFont = new Font(display, boldFontData);

		FontData[] italicFontData = getModifiedFontData(plainFont.getFontData(), SWT.ITALIC);
		italicFont = new Font(display, italicFontData);

		boldStyler = new Styler() {
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = display.getSystemColor(SWT.COLOR_DARK_BLUE);
				textStyle.background = null;
				textStyle.font = boldFont;
			}
		};

		italicStyler = new Styler() {
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = display.getSystemColor(SWT.COLOR_DARK_GREEN);
				textStyle.background = null;
				textStyle.font = italicFont;
			}
		};

		plainStyler = new Styler() {
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = display.getSystemColor(SWT.COLOR_BLACK);
				textStyle.background = null;
				textStyle.font = plainFont;
			}
		};
	}

	protected void updateTextLayout(GridItem gridItem, TextLayout textLayout) {
		StyledString styledString = getStyledText(gridItem.getData());
		textLayout.setText(styledString.getString());
		for (StyleRange styleRange : styledString.getStyleRanges()) {
			textLayout.setStyle(styleRange, styleRange.start, styleRange.start + styleRange.length);
		}
	}

	private StyledString getStyledText(Object element) {
		StyledString result = null;
		if (element instanceof ShelvesetDiscussionItem) {
			ShelvesetDiscussionItem item = (ShelvesetDiscussionItem) element;
			String discussionComment = item.getComment();
			String author = item.getAuthorDisplayName();
			String lastUpdated = DateUtil.ageAsPrettyString(item.getLastUpdatedDate(), "ago");

			result = new StyledString();
			if (discussionComment != null) {
				result.append(author, boldStyler);
				result.append("- ");
				result.append(lastUpdated, italicStyler);
				result.append("\n");
				result.append(discussionComment, plainStyler);

			}
		}
		return result;
	}

	private static FontData[] getModifiedFontData(FontData[] originalData, int additionalStyle) {
		FontData[] styleData = new FontData[originalData.length];
		for (int i = 0; i < styleData.length; i++) {
			FontData base = originalData[i];
			styleData[i] = new FontData(base.getName(), base.getHeight(), base.getStyle() | additionalStyle);
		}
		return styleData;
	}

	public boolean isWordWrap() {
		return true;
	}

}
