package com.samphippen.explaygamejam2012;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CogGraph {

	private HashMap<Cog, List<Cog>> mGraph = new HashMap<Cog, List<Cog>>();
	
	public Cog mDrive;
	public Cog mScrew;
	
	public List<Cog> mCogs = new ArrayList<Cog>();
	private List<Cog> mPossibleConnetions = new ArrayList<Cog>();	

	public CogGraph() {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        Texture t = ResourceManager.get("bigcog");
		mDrive = new Cog(new Sprite(t), 0, 0, 1900);
		mDrive.promoteToDrive();     
		
		mDrive.setCenterX(GameHolder.CanvasSizeX * 0.5f);
		mDrive.setCenterY(80);        
		mDrive.fixToGrid(); 

		mCogs.add(mDrive);

		mGraph.put(mDrive, new ArrayList<Cog>());
		
		mScrew = Cog.getCog(1);
		mScrew.promoteToScrew();     
		
		mScrew.setCenterX(GameHolder.CanvasSizeX * 0.5f);
		mScrew.setCenterY(GameHolder.CanvasSizeY - 233);
		//mScrew.setCenterY((1280-75) - 123);  
		mScrew.fixToGrid(); 

		mCogs.add(mScrew);
	}

	public Cog touchOnCog(int x, int y) {
		
		for (int i = 0; i < mCogs.size(); i++) {
			Cog other = mCogs.get(i);

			if (other.getIsFixed() == true)
				continue;
			
			if (other.isTouchOn(x, y) == true) {
				
				Logger.println("Touch On " + other.CogID);
				
				removeCog(other);
				
				mCogs.add(other);
				
				refactorForward(); 
				
				return other; 
			}
		}
		
		return null;
	}
	
	public boolean checkDropCog(Cog cog) { 

		//cog.setFailed(false);
		
		// this is possibly not the best way to do this?!
		mPossibleConnetions.clear();

		// ***********  find all connecting cogs *********** 

		for (int i = 0; i < mCogs.size(); i++) {
			Cog other = mCogs.get(i);			
			
			other.setHighlight(false);
			other.setFailed(false);
			
			if (other == cog)
				continue;

			if (cog.isPossibleOverlapping(other) == true) {
				mPossibleConnetions.add(other);
			}
		}

		// *********** find a suitable spot *********** 
		
		boolean hasFailed = false; 
		for (int i = 0; i < mPossibleConnetions.size(); i++) {
			Cog other = mPossibleConnetions.get(i);

			if (cog.isOverlapping(other) == true) {
			
				cog.setFailed(true); 
				other.setFailed(true);
				
				// THIS NEEDS BETTER(ING) 
				hasFailed = true; 
			}
		}
		
		if (hasFailed == true) return false; 
		
		for (int i = 0; i < mPossibleConnetions.size(); i++) {
			Cog other = mPossibleConnetions.get(i);

			other.setHighlight(true);
		}
		
		boolean success = mPossibleConnetions.size() > 0; 
		
		if (success == true) { 
			cog.setHighlight(true);
		}
		
		return success; 
	}

	public boolean dropCog(Cog cog) {

		cog.setFailed(false);
		
		Logger.println("Drop Cog " + cog.CogID);
		
		// this is possibly not the best way to do this?!
		mPossibleConnetions.clear();

		// ***********  find all connecting cogs *********** 

		for (int i = 0; i < mCogs.size(); i++) {
			Cog other = mCogs.get(i);
			
			other.setHighlight(false);
			other.setFailed(false);
			
			if (other == cog)
				continue;

			if (cog.isPossibleOverlapping(other) == true) {
				mPossibleConnetions.add(other);
			}
		}

		// *********** find a suitable spot *********** 
		
		for (int i = 0; i < mPossibleConnetions.size(); i++) {
			Cog other = mPossibleConnetions.get(i);

			if (cog.isOverlapping(other) == true) {
				
				// THIS NEEDS BETTER(ING) 
				return false;
			}
		}

		// *********** refactor cog graph *********** 

		boolean isAttachedToDrive = false;

		// scan for cogs that have been visited
		for (int i = 0; i < mPossibleConnetions.size(); i++) {
			Cog other = mPossibleConnetions.get(i);

			if (other.mVisited == true) {
				isAttachedToDrive = true;
				add(other, cog);
			}
		}

		/*
		if (isAttachedToDrive == true) {

			for (int i = 0; i < mCogs.size(); i++) {
				mCogs.get(i).mBindingsReversed = false;
			}

			// scan for cogs that were not visited (not attached to drive) 
			for (int i = 0; i < mPossibleConnetions.size(); i++) {
				Cog other = mPossibleConnetions.get(i);

				if (other.mVisited == false && other.mBindingsReversed == false) {
					reverseBindings(other);

					add(cog, other);
				}
			}
		} else {
			*/
			// we can safely link the cogs in anyway we like? 
			for (int i = 0; i < mPossibleConnetions.size(); i++) {
				Cog other = mPossibleConnetions.get(i);
				
				if (other.mVisited == false) {
					add(other, cog);
				}
			}
		//}
		
		refactorForward(); 

		return true;
	}

	private void reverseBindings(Cog cog) {

		Logger.println("Reverse Bindings " + cog.CogID);
		
		cog.mVisited = true;

		// Check any forward bindings
		if (mGraph.containsKey(cog) == true) {

			List<Cog> list = mGraph.get(cog);

			for (int i = list.size() - 1; i >= 0; --i) {

				Cog other = list.get(i);

				if (other.mVisited == false) {
					
					remove(cog, other);
					
					reverseBindings(other);
					
					add(other, cog);			
				}
			}
		}

		// check any backward bindings
		for (int i = cog.mConnections.size() - 1; i >= 0; --i) {

			Cog other = cog.mConnections.get(i);

			if (other.mVisited == false) {

				remove(other, cog);
				
				reverseBindings(other);
				
				add(cog, other);				
			}
		}
	}

	public boolean refactorForward() {

		mDrive.setFailed(false); 
		mDrive.setHighlight(false);
		
		mScrew.setFailed(false); 
		mScrew.setHighlight(false);
		
		for (int i = 0; i < mCogs.size(); i++) {
			mCogs.get(i).mVisited = false;
		}

		if (refactorForward_Sub(mDrive) == true) {
			
			return true;
		} else {
			return false;
		}
	}

	private boolean refactorForward_Sub(Cog node) {

		node.mVisited = true;

		// check any backward bindings
		for (int i = node.mConnections.size() - 1; i >= 0; --i) {

			Cog other = node.mConnections.get(i);

			if (other.mVisited == false) {

				remove(other, node);				
				//reverseBindings(other);				
				add(node, other);				
			}
		}
		
		if (mGraph.containsKey(node)) {
			List<Cog> list = mGraph.get(node);

			for (int i = 0; i < list.size(); i++) {
				Cog child = list.get(i);

				if (child.mVisited == false) {
					refactorForward_Sub(child);
				}
			}
		}	

		return true;
	}

	public void addCog(Cog cog) {
		
		Logger.println("Add Cog " + cog.CogID);
		
		cog.mAngle = 0; 
		
		mCogs.add(cog);
	}

	public void removeCog(Cog cog) {

		cog.mAngle = 0;
		
		Logger.println("Remove Cog " + cog.CogID);
		
		// Check any forward bindings
		if (mGraph.containsKey(cog) == true) {

			List<Cog> list = mGraph.get(cog);

			for (int i = list.size() - 1; i >= 0; --i) {

				Cog other = list.get(i);

				remove(cog, other);
			}
		}

		// check any backward bindings
		for (int i = cog.mConnections.size() - 1; i >= 0; --i) {

			Cog other = cog.mConnections.get(i);

			remove(other, cog);				
		}

		mCogs.remove(cog);
	}
	
	public void clear() {
		
		for (int i = mCogs.size() - 1; i >= 0; --i) {
			
			Cog cog = mCogs.get(i); 
			
			if (cog.getIsFixed() == false){ 
				removeCog(cog); 
			}
		}

	}

	public boolean remove(Cog parent, Cog cog) {		
		
		if (mGraph.containsKey(parent) == false) {
			// return false;
			return false;
		}

		List<Cog> list = mGraph.get(parent);

		if (list.contains(cog) == false) {
			return false;
		}

		Logger.println("Remove " + cog.CogID + " from " + parent.CogID);
		
		// remove the back link 
		if (cog.mConnections.contains(parent) == true) {
			cog.mConnections.remove(parent);
		}

		list.remove(cog);

		return true;
	}

	public boolean add(Cog parent, Cog cog) {

		if (mGraph.containsKey(parent) == false) {
			
			Logger.println("Add Node " + parent.CogID);
			
			mGraph.put(parent, new ArrayList<Cog>());
		}

		List<Cog> list = mGraph.get(parent);

		if (list.contains(cog) == true) {
			return false;
		}

		Logger.println("Add " + cog.CogID + " to " + parent.CogID);
		
		// we need a back link so we can refactor the graph 
		if (cog.mConnections.contains(parent) == false) {
			cog.mConnections.add(parent);
		}

		list.add(cog);

		return true;
	}

	public boolean evaluate() {
		mDrive.reset();
		mScrew.reset(); 
		
		for (int i = 0; i < mCogs.size(); i++) {
			mCogs.get(i).mVisited = false;
		}

		mDrive.rotate(false);

		if (propogate(mDrive, true) == true) {

			for (int i = 0; i < mCogs.size(); i++) {
				mCogs.get(i).applyRotation();
			}

			return true;
		} else {
		    SoundSystem.playWithDelay("CogsJammed", 3900);
		    
			return false;
		}
	}

	private boolean propogate(Cog node, boolean dir) {

		node.mVisited = true;
		node.mDir = dir; 
		boolean accum = true;

		if (mGraph.containsKey(node)) {
			List<Cog> list = mGraph.get(node);

			for (int i = 0; i < list.size(); i++) {
				Cog child = list.get(i);

				child.rotate(dir);				

				if (child.mVisited == false) {
					accum &= propogate(child, !dir);
				}
				else { 
					//accum &= !child.mVisited;
					accum &= !(node.mDir == child.mDir); 
				}
			}
		}

		return accum;
	}

	public void renderDebugLines(ShapeRenderer shape) {

		for (int i = 0; i < mCogs.size(); i++) {
			mCogs.get(i).mVisited = false;
			//mCogs.get(i).mBindingsReversed = false;
		}

		renderDebugLines_Sub(shape, mDrive, true);

		for (int i = 0; i < mCogs.size(); i++) {
			if (mCogs.get(i).mVisited == false) {

				renderDebugLines_Unconneted(shape, mCogs.get(i));
			}
		}
	}

	private boolean renderDebugLines_Sub(ShapeRenderer shape, Cog node,
			boolean dir) {
		node.mVisited = true;

		//boolean accum = true;

		if (mGraph.containsKey(node)) {

			List<Cog> list = mGraph.get(node);

			for (int i = 0; i < list.size(); i++) {

				Cog child = list.get(i);

				if (node.mDir == true && child.mDir == false) 
					shape.setColor(0, 1, 0, 1);
				else if (node.mDir == false && child.mDir == true) 
					shape.setColor(1, 0, 1, 1);
				else 
					shape.setColor(1, 0, 0, 1);

				shape.line(node.getCenterX(), node.getCenterY(),
						child.getCenterX(), child.getCenterY());

				if (child.mVisited == false) {
					renderDebugLines_Sub(shape, child, !dir);
				}
			}
		}

		return true;
	}

	private void renderDebugLines_Unconneted(ShapeRenderer shape, Cog node) {

		node.mVisited = true; 
		
		if (mGraph.containsKey(node)) {

			List<Cog> list = mGraph.get(node);

			for (int i = 0; i < list.size(); i++) {

				Cog child = list.get(i);

				shape.setColor(0, 0, 1, 1);

				shape.line(node.getCenterX(), node.getCenterY(),
						child.getCenterX(), child.getCenterY());

				if (child.mVisited == false) {
					renderDebugLines_Unconneted(shape, child);
				}
			}
		}
	}
}
