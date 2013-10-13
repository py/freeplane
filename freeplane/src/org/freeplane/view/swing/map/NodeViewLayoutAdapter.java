/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.view.swing.map;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.freeplane.features.cloud.CloudModel;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.mode.ModeController;
import org.freeplane.features.nodestyle.NodeStyleController;
import org.freeplane.view.swing.map.cloud.CloudView;

abstract public class NodeViewLayoutAdapter implements INodeViewLayout {
	protected static class LayoutData {
		final int[] lx;
		final int[] ly;
		final boolean[] free;
		final boolean[] summary;
		int left;
		int childContentHeight;
		int top;
		boolean firstDataSet;

		public LayoutData(int childCount) {
			super();
			this.lx = new int[childCount];
			this.ly = new int[childCount];
			this.free = new boolean[childCount];
			this.summary = new boolean[childCount];
			this.left = 0;
			this.childContentHeight = 0;
			this.top = 0;
			firstDataSet = false;
		}
	}

	private static Dimension minDimension;
	private int childCount;
	private JComponent content;
	protected Point location = new Point();
	private NodeModel model;
	private int spaceAround;
	private int vGap;
	private NodeView view;
	private int contentWidth;
	private int contentHeight;
	private int cloudHeight;

	public void addLayoutComponent(final String arg0, final Component arg1) {
	}

	/**
	 * @return Returns the childCount.
	 */
	protected int getChildCount() {
		return childCount;
	}

	/**
	 * @return Returns the content.
	 */
	protected JComponent getContent() {
		return content;
	}

	/**
	 * @return Returns the model.
	 */
	protected NodeModel getModel() {
		return model;
	}

	/**
	 * @return Returns the spaceAround.
	 */
	int getSpaceAround() {
		return spaceAround;
	}

	/**
	 * @return Returns the vGap.
	 */
	int getVGap() {
		return vGap;
	}

	/**
	 * @return Returns the view.
	 */
	protected NodeView getView() {
		return view;
	}

	abstract protected void layout();

	public void layoutContainer(final Container c) {
		if (setUp(c)) {
			layout();
		}
		shutDown();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	public Dimension minimumLayoutSize(final Container arg0) {
		if (NodeViewLayoutAdapter.minDimension == null) {
			NodeViewLayoutAdapter.minDimension = new Dimension(0, 0);
		}
		return NodeViewLayoutAdapter.minDimension;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	public Dimension preferredLayoutSize(final Container c) {
		if (!c.isValid()) {
			c.validate();
		}
		return c.getSize();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	public void removeLayoutComponent(final Component arg0) {
	}

	protected boolean setUp(final Container c) {
		final NodeView localView = (NodeView) c;
		JComponent content = localView.getContent();
		if (content == null)
			return false;
		final int localChildCount = localView.getComponentCount() - 1;
		for (int i = 0; i < localChildCount; i++) {
			final Component component = localView.getComponent(i);
			((NodeView) component).validateTree();
		}
		this.content = content;
		view = localView;
		model = localView.getModel();
		childCount = localChildCount;
		if (getModel().isVisible()) {
			setVGap(getView().getVGap());
		}
		else {
			setVGap(getView().getVisibleParentView().getVGap());
		}
		spaceAround = view.getSpaceAround();
		if (view.isContentVisible()) {
			final Dimension contentSize = calculateContentSize(view);
			contentWidth = contentSize.width;
			contentHeight = contentSize.height;
			cloudHeight = getAdditionalCloudHeigth(view);
		}
		else {
			contentHeight = 0;
			contentWidth = 0;
			cloudHeight = 0;
		}
		return true;
	}

	protected Dimension calculateContentSize(final NodeView view) {
		final JComponent content = view.getContent();
		final ModeController modeController = view.getMap().getModeController();
		final NodeStyleController nsc = NodeStyleController.getController(modeController);
		Dimension contentSize;
		if (content instanceof ZoomableLabel) {
			int maxNodeWidth = nsc.getMaxWidth(view.getModel());
			contentSize = ((ZoomableLabel) content).getPreferredSize(maxNodeWidth);
		}
		else {
			contentSize = content.getPreferredSize();
		}
		int minNodeWidth = nsc.getMinWidth(view.getModel());
		int contentWidth = Math.max(view.getZoomed(minNodeWidth), contentSize.width);
		int contentHeight = contentSize.height;
		final Dimension contentProfSize = new Dimension(contentWidth, contentHeight);
		return contentProfSize;
	}

	protected void shutDown() {
		view = null;
		model = null;
		content = null;
		childCount = 0;
		setVGap(0);
		spaceAround = 0;
	}

	public void setVGap(final int vGap) {
		this.vGap = vGap;
	}

	/**
	 * Calculates the tree height increment because of the clouds.
	 */
	public int getAdditionalCloudHeigth(final NodeView node) {
		if (!node.isContentVisible()) {
			return 0;
		}
		final CloudModel cloud = node.getCloudModel();
		if (cloud != null) {
			return CloudView.getAdditionalHeigth(cloud, node);
		}
		else {
			return 0;
		}
	}

	private int[] levels(List<Integer> childrenOnSide, int highestSummaryLevel) {
	    final int[] levels = new int[childrenOnSide.size() + 1];
		{
			boolean useNextSummaryAsItem = true;
			int level = levels[0] = highestSummaryLevel;
			int levelIndex = 1;
			for (int i : childrenOnSide) {
				final NodeView child = (NodeView) getView().getComponent(i);
				final boolean isItem = !child.isSummary() || useNextSummaryAsItem;
				if (isItem)
	                level = 0;
                else
	                level++;
				levels[levelIndex] = level;
				levelIndex++;
				if (isItem) {
					final int childHeight = child.getHeight() - 2 * getSpaceAround();
					if (childHeight != 0)
						useNextSummaryAsItem = false;
					else if (level > 0)
						useNextSummaryAsItem = true;
				}
			}
		}
	    return levels;
    }

	protected void calcLayout(final boolean calculateOnLeftSide, final LayoutData data) {
		List<Integer> childrenOnSide = childrenOnSide(calculateOnLeftSide);
		if (childrenOnSide.size() == 0)
			return;
		int childContentHeightSum = 0;
		int top = 0;
		int y = 0;
		boolean visibleChildFound = false;
		final int highestSummaryLevel = highestSummaryLevel(childrenOnSide);
		final GroupMargins[] groups = GroupMargins.create(highestSummaryLevel);
		final int[] groupStartContentHeightSum = new int[highestSummaryLevel];
		final int summaryBaseX[] = new int[highestSummaryLevel];
		final int[] levels = levels(childrenOnSide, highestSummaryLevel);
		int levelIndex = 1;
		for (int childIndex : childrenOnSide) {
			final NodeView child = (NodeView) getView().getComponent(childIndex);
			int level = levels[levelIndex];
			final int oldLevel = levels[levelIndex - 1];
			data.summary[childIndex] = level > 0;
			data.free[childIndex] = child.isFree();
			final ChildPositionCalculator childPositionCalculator = ChildPositionCalculator.create(child, spaceAround, vGap, oldLevel, level);
			childPositionCalculator.calcChildY(childIndex, y, visibleChildFound, calculateOnLeftSide, data, levels, groups);
			top += childPositionCalculator.getTopChange();
			y = childPositionCalculator.getY();
			childPositionCalculator.chilContentHeightSum(groupStartContentHeightSum, visibleChildFound, childContentHeightSum);
			childContentHeightSum = childPositionCalculator.getChildContentHeightSum();
			childPositionCalculator.calcChildRelativeXPosition(data, summaryBaseX, childIndex, contentWidth);
			visibleChildFound = visibleChildFound || childPositionCalculator.isChildVisible();
			levelIndex++;
		}
		top += (contentHeight - childContentHeightSum) / 2;
		int left = calculateLeft(childrenOnSide, data);
		setData(data, calculateOnLeftSide, left, childContentHeightSum, top);
	}


	private int calculateLeft(List<Integer> childrenOnSide, final LayoutData data) {
	    int left = 0;
		for (int i : childrenOnSide) {
			left = Math.min(left, data.lx[i]);
		}
		return left;
    }

	private int highestSummaryLevel(List<Integer> childrenOnSide) {
	    int highestSummaryLevel = 1;
		{
			int level = 1;
			for (int i : childrenOnSide) {
				final NodeView child = (NodeView) getView().getComponent(i);
				if (child.isSummary()) {
					level++;
					highestSummaryLevel = Math.max(highestSummaryLevel, level);
				}
				else {
					level = 1;
				}
			}
		}
	    return highestSummaryLevel;
    }

	private List<Integer> childrenOnSide(final boolean isLeft) {
	    List<Integer> childrenOnSide = new ArrayList<Integer>(getChildCount());
		for (int i = 0; i < getChildCount(); i++) {
			final NodeView child = (NodeView) getView().getComponent(i);
			if (child.isLeft() == isLeft) {
				childrenOnSide.add(i);
			}
		}
	    return childrenOnSide;
    }

	private void setData(final LayoutData data, boolean isLeft, int left, int childContentHeight, int top) {
		if (!data.firstDataSet)
	        setOneSideData(data, left, childContentHeight, top);
        else
	        setOtherSideData(data, isLeft, left, childContentHeight, top);
	}

	private void setOneSideData(final LayoutData data, int left, int childContentHeight, int top) {
	    data.left = left;
	    data.childContentHeight = childContentHeight;
	    data.top = top;
		data.firstDataSet = true;
    }

	private void setOtherSideData(final LayoutData data, boolean isLeft, int left, int childContentHeight, int top) {
	    data.left = Math.min(data.left, left);
	    data.childContentHeight = Math.max(data.childContentHeight, childContentHeight);
	    int deltaTop = top - data.top;
	    final boolean changeLeft;
	    if (deltaTop < 0) {
	    	data.top = top;
	    	changeLeft = !isLeft;
	    	deltaTop = -deltaTop;
	    }
	    else {
	    	changeLeft = isLeft;
	    }
	    for (int i = 0; i < getChildCount(); i++) {
	    	NodeView child = (NodeView) getView().getComponent(i);
	    	if (child.isLeft() == changeLeft && (data.summary[i] || !data.free[i])) {
	    		data.ly[i] += deltaTop;
	    	}
	    }
    }

	protected void setChildLocationsAndOwnSize(final LayoutData data) {
		setContentVisibility();
		final int contentX = Math.max(getSpaceAround(), -data.left);
		int contentY = getSpaceAround() + cloudHeight / 2 - Math.min(0, data.top);
		int baseY = contentY - getSpaceAround() + data.top;
		int minY = findMinimumY(data, contentY, baseY);
		if (minY < 0) {
			contentY -= minY;
			baseY -= minY;
		}
		getContent().setBounds(contentX, contentY, contentWidth, contentHeight);
		setChildLocations(contentX, contentY, data, baseY);
		setOwnSizeAndOverlap(data, contentX, contentY, -minY);
	}

	private void setChildLocations(final int contentX, int contentY, final LayoutData data, int baseY) {
	    for (int i = 0; i < getChildCount(); i++) {
			NodeView child = (NodeView) getView().getComponent(i);
			final int x = contentX + data.lx[i];
			final int y = (!data.summary[i] && data.free[i] ? contentY : baseY) + data.ly[i];
			child.setLocation(x, y);
		}
    }

	private void setOwnSizeAndOverlap(final LayoutData data, final int contentX, int contentY, int topOverlap) {
	    int width = contentX + contentWidth + getSpaceAround();
		int height = contentY + contentHeight + cloudHeight / 2 + getSpaceAround();
		int heigthWithoutOverlap = height;
		for (int i = 0; i < getChildCount(); i++) {
			NodeView child = (NodeView) getView().getComponent(i);
			final int y = child.getY();
			if (!data.free[i])
				heigthWithoutOverlap = Math.max(heigthWithoutOverlap,
					y + child.getHeight() + cloudHeight / 2 - child.getBottomOverlap());
			width = Math.max(width, child.getX() + child.getWidth());
			height = Math.max(height, y + child.getHeight() + cloudHeight / 2);
		}
		view.setSize(width, height);
		view.setTopOverlap(topOverlap);
		view.setBottomOverlap(height - heigthWithoutOverlap);
    }

	private int findMinimumY(final LayoutData data, int contentY, int baseY) {
	    int minY = 0;
		for (int i = 0; i < getChildCount(); i++) {
			minY = Math.min(minY, (!data.summary[i] && data.free[i] ? contentY : baseY) + data.ly[i]);
		}
	    return minY;
    }

	private void setContentVisibility() {
	    if (getView().isContentVisible())
	        getContent().setVisible(true);
        else
	        getContent().setVisible(false);
    }
}
