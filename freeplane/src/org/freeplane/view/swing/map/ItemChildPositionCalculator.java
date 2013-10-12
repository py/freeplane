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

import org.freeplane.view.swing.map.NodeViewLayoutAdapter.LayoutData;

/**
 * @author Dimitry Polivaev
 * 12.10.2013
 */
public class ItemChildPositionCalculator extends ChildPositionCalculator{
	public ItemChildPositionCalculator(int spaceAround, int vGap, NodeView child, int previousChildLevel, int level) {
        super(spaceAround, vGap, child, previousChildLevel, level);
    }

	@Override
    public void calcChildY(int childIndex, int yBefore, boolean visibleChildAlreadyFound, final boolean calculateOnLeftSide, final LayoutData data, final int[] levels, final GroupMargins[] groups) {
		initY(yBefore, visibleChildAlreadyFound, data, childIndex);
		this.calcItemY(data, groups, childIndex, visibleChildAlreadyFound);
	}

	public void calcItemY(final LayoutData data, final GroupMargins[] groups, int i, final boolean visibleChildFound) {
	    final boolean followsSummary = previousChildLevel > 0;
	    if (followsSummary)
	        for (int j = 0; j < previousChildLevel; j++)
	    		groups[j].beginFrom(i);
	    else if (child.isFirstGroupNode())
	        groups[0].start = i;

	    if (childShiftY < 0 || !visibleChildFound)
	        top += childShiftY;
	    y -= child.getTopOverlap();
	    if (childShiftY < 0) {
	    	data.ly[i] = y;
	    	y -= childShiftY;
	    }
	    else {
	    	if (visibleChildFound)
	    		y += childShiftY;
	    	data.ly[i] = y;
	    }
	    if (childHeight != 0) {
	    	y += childHeight + getVGap();
	    	y -= child.getBottomOverlap();
	    }
	    top -= childContentShift;
	    top += child.getTopOverlap();
    	setGroupMargins(data, groups, i);
    }

	protected void calcItemChildContentHeightSum(final int[] groupStartContentHeightSum, boolean visibleChildFound) {
	    childContentHeightSum += childContentHeight;
	    final boolean followsSummary = previousChildLevel > 0;
	    if (followsSummary)
	        for (int j = 0; j < previousChildLevel; j++)
	    		groupStartContentHeightSum[j] = childContentHeightSum;
	    else if (child.isFirstGroupNode()) {
	    	groupStartContentHeightSum[0] = childContentHeightSum;
	    }
	    if (childHeight != 0 && visibleChildFound)
	        childContentHeightSum += getVGap();
    }

	@Override
    public void chilContentHeightSum(final int[] groupStartContentHeightSum, boolean pVisibleChildFound, int childContentHeightSumBefore) {
		childContentHeightSum = childContentHeightSumBefore;
		this.calcItemChildContentHeightSum(groupStartContentHeightSum, pVisibleChildFound);
    }
}