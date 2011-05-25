package com.josephblough.fluville;

import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.util.pool.GenericPool;

public class RectanglePool extends GenericPool<Rectangle> {

	@Override
	protected Rectangle onAllocatePoolItem() {
		// TODO Auto-generated method stub
		return new Rectangle(0, 0, 0, 0);
	}

	public Rectangle obtain(final float pX, final float pY, final float pWidth, final float pHeight) {
		Rectangle rectangle = super.obtainPoolItem();
		rectangle.setPosition(pX, pY);
		rectangle.setWidth(pWidth);
		rectangle.setHeight(pHeight);
		return rectangle;
	}
}
