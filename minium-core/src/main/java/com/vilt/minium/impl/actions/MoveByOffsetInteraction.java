/*
 * Copyright (C) 2013 The Minium Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vilt.minium.impl.actions;

import com.vilt.minium.CoreWebElements;

/**
 * The Class MoveByOffsetInteraction.
 */
public class MoveByOffsetInteraction extends MouseInteraction {

	private int xOffset;
	private int yOffset;

	/**
	 * Instantiates a new move by offset interaction.
	 *
	 * @param source the source
	 * @param xOffset the x offset
	 * @param yOffset the y offset
	 */
	public MoveByOffsetInteraction(CoreWebElements<?> source, int xOffset, int yOffset) {
		super(source);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/* (non-Javadoc)
	 * @see com.vilt.minium.actions.DefaultInteraction#doPerform()
	 */
	@Override
	protected void doPerform() {
		getActions().moveByOffset(xOffset, yOffset).perform();
	}
}
