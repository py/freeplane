/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2013 Dimitry
 *
 *  This file author is Dimitry
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

import org.freeplane.features.cloud.CloudModel;
import org.freeplane.features.nodelocation.LocationModel;
import org.freeplane.view.swing.map.NodeViewLayoutAdapter.LayoutData;
import org.freeplane.view.swing.map.cloud.CloudView;

/**
 * @author Dimitry Polivaev
 * 12.10.2013
 */
abstract class ChildPositionCalculator{
	public static ChildPositionCalculator create(NodeView child, int spaceAround, int vGap, int previousChildLevel, int level) {
	    if (level == 0) {
	    	if (child.isFree())
	            return new FreeItemChildPositionCalculator(spaceAround, vGap, child, previousChildLevel, level);
            else
	            return new ItemChildPositionCalculator(spaceAround, vGap, child, previousChildLevel, level);
	    }
        else
        	if (child.isFree())
        		return new FreeSummaryChildPositionCalculator(spaceAround, vGap, child, previousChildLevel, level);
        	else
        		return new SummaryChildPositionCalculator(spaceAround, vGap, child, previousChildLevel, level);
    }

	protected final NodeView child;
	protected final int previousChildLevel;
	protected final int level;
	protected final int vGap;
	protected final int spaceAround;
	protected final int childHeight;
	protected final int childCloudHeigth;
	protected final int childContentHeight;
	protected final int childShiftY;
	protected final int childContentShift;
	protected final boolean isItem;

	protected int top;
	public int getTopChange() {
		return top;
	}

	public int getY() {
		return y;
	}

	protected int y;
	protected int childContentHeightSum;


	public ChildPositionCalculator(int spaceAround, int vGap, NodeView child, int previousChildLevel, int level) {
		this.vGap = vGap;
		this.spaceAround = spaceAround;
		this.child = child;
		this.previousChildLevel = previousChildLevel;
		this.level = level;
		childHeight = child.getHeight() - 2 * spaceAround;
		childCloudHeigth = getAdditionalCloudHeigth();
		childContentHeight = child.getContent().getHeight() + childCloudHeigth;
		childShiftY = child.isContentVisible() ? child.getShift() : 0;
		childContentShift = child.getContent().getY() - childCloudHeigth / 2 -spaceAround;
		isItem = level == 0;
    }

	public int getAdditionalCloudHeigth() {
		if (!child.isContentVisible()) {
			return 0;
		}
		final CloudModel cloud = child.getCloudModel();
		if (cloud != null) {
			return CloudView.getAdditionalHeigth(cloud, child);
		}
		else {
			return 0;
		}
	}

	public void calcChildY(int childIndex, int yBefore, boolean visibleChildAlreadyFound, final boolean calculateOnLeftSide, final LayoutData data, final int[] levels, final GroupMargins[] groups){
		initY(yBefore, visibleChildAlreadyFound, data, childIndex);
	}

	protected void initY(int yBefore, boolean visibleChildAlreadyFound, final LayoutData data, int i) {
	    this.top = 0;
		this.y = yBefore;
    }

	protected void setGroupMargins(final LayoutData data, final GroupMargins[] groups, int i) {
		groups[level].setMargins(child.isFirstGroupNode(), data.ly[i] + child.getTopOverlap(), data.ly[i] + childHeight - child.getBottomOverlap());
    }

	protected int getVGap() {
	    return vGap;
    }

	protected int getSpaceAround() {
	    return spaceAround;
    }

	public void chilContentHeightSum(final int[] groupStartContentHeightSum, boolean pVisibleChildFound, int childContentHeightSumBefore) {
		childContentHeightSum = childContentHeightSumBefore;
    }

	public int getChildContentHeightSum() {
	    return childContentHeightSum;
    }

	public void calcChildRelativeXPosition(final LayoutData data, final int[] summaryBaseX, int childIndex, int parentContentWidth) {
	    final int x;
	    final int baseX;
	    baseX = calcBaseX(summaryBaseX, parentContentWidth);
	    final int childHGap = calcHGap();
	    if (child.isLeft()) {
	    	x = baseX - childHGap - child.getContent().getX() - child.getContent().getWidth();
	    	summaryBaseX[level] = Math.min(summaryBaseX[level], x + getSpaceAround());
	    }
	    else {
	    	x = baseX + childHGap - child.getContent().getX();
	    	summaryBaseX[level] = Math.max(summaryBaseX[level], x + child.getWidth() - getSpaceAround());
	    }
	    data.lx[childIndex] = x;
    }

	private int calcHGap() {
	    final int childHGap;
	    if (child.isContentVisible())
	    	childHGap = child.getHGap();
	    else if (child.isSummary())
	    	childHGap = child.getZoomed(LocationModel.HGAP);
	    else
	    	childHGap = 0;
	    return childHGap;
    }

	private int calcBaseX(final int[] summaryBaseX, int parentContentWidth) {
	    final int baseX;
	    if (isItem) {
	    	if (!child.isFree()) {
	    		if (previousChildLevel > 0 || child.isFirstGroupNode())
	    			summaryBaseX[0] = 0;
	    	}
	    	baseX = child.isLeft() != child.isFree() ? 0 : parentContentWidth;
	    }
	    else {
	    	if (child.isFirstGroupNode())
	    		summaryBaseX[level] = 0;
	    	baseX = summaryBaseX[level - 1];
	    }
	    return baseX;
    }

	public boolean isChildVisible() {
		return childHeight != 0;
	}
}