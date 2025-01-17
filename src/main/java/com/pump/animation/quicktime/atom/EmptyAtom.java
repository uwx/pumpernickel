/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.animation.quicktime.atom;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.pump.io.GuardedOutputStream;

/**
 * This is an empty atom. (I'm not sure why these exist, but they do.)
 */
public class EmptyAtom extends Atom {

	public EmptyAtom(Atom parent) {
		super(parent);
	}

	@Override
	public String getIdentifier() {
		return null;
	}

	@Override
	protected long getSize() {
		return 0;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) {
	}

	@Override
	public Enumeration<Object> children() {
		return EMPTY_ENUMERATION;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public Atom getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		return -1;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
}