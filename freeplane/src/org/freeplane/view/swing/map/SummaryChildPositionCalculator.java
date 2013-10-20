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


/**
 * @author Dimitry Polivaev
 * 12.10.2013
 */
public class SummaryChildPositionCalculator extends ChildPositionCalculator{
	public SummaryChildPositionCalculator(int spaceAround, int vGap, NodeView child, int oldLevel, int level) {
        super(spaceAround, vGap, child, oldLevel, level);
    }

	@Override
    public void calcChildY(int childIndex, int yBefore, boolean visibleChildAlreadyFound, final boolean calculateOnLeftSide, final int[] levels, final GroupMargins[] groups) {
		final GroupMargins groupMargins = groups[level - 1];
		childBeginY = (groupMargins.startY + groupMargins.endY) / 2 - childContentHeight / 2
		        + childShiftY - (child.getContent().getY() - childCloudHeigth / 2 - getSpaceAround());
		int summaryY = childBeginY;
		if (!child.isFree()) {
			if (childHeight != 0) {
				summaryY = childBeginY + childHeight + getVGap() - child.getBottomOverlap();
			}
			childEndY = Math.max(yBefore, summaryY);
			groupedItemShiftRequiredBySummary = groupMargins.startY - childBeginY - child.getTopOverlap();
			if(groupedItemShiftRequiredBySummary > 0){
				childEndY += groupedItemShiftRequiredBySummary;
				topChange -= groupedItemShiftRequiredBySummary;
			}
		}
		else
			childEndY = yBefore;
	}

	protected void calculateSummaryChildContentHeightSum(final int[] groupStartContentHeightSum) {
	    final int itemLevel = level - 1;
	    if (child.isFirstGroupNode())
	        groupStartContentHeightSum[level] = groupStartContentHeightSum[itemLevel];
	    if (!child.isFree()) {
	    	final int summaryContentHeight = groupStartContentHeightSum[itemLevel] + childContentHeight;
	    	if (childContentHeightSum < summaryContentHeight) {
	    		childContentHeightSum = summaryContentHeight;
	    	}
	    }
    }

	@Override
    public void calcChildContentHeightSum(final int[] groupStartContentHeightSum, boolean pVisibleChildFound, int childContentHeightSumBefore) {
		childContentHeightSum = childContentHeightSumBefore;
	    calculateSummaryChildContentHeightSum(groupStartContentHeightSum);
    }

}