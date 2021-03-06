package com.samphippen.explaygamejam2012;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cog {

	static int mCogID = 0;
	
    public static Cog getCog(int i) {
        Cog c = new Cog(new Sprite(ResourceManager.get("cog" + i)), i, 0, mCogID++);
        return c;
    }

    private Sprite mSprite;
    // THIS MUST BE DEGREEEEES
    public float mAngle;
    private boolean mMouseTracking = false;
    private boolean mIsDrive = false; 
	private boolean mIsScrew = false;  
	private boolean mCanPlace = false;
	
	public boolean mVisited;
	private float mNextAngle;
	public boolean mBindingsReversed;
	public int CogID = 0;
	
	// we need this for refactoring the graph (probably)
	public List<Cog> mConnections = new ArrayList<Cog>();
	public int mSize;	
	
	public boolean mHighlight = false;
	public boolean mFailed = false;
	public boolean mDir;
	
    public Cog(Sprite s, int size, float a, int id) {    	
        mSprite = s;
        mSize = size; 
        mAngle = a;
        CogID = id; 
    }
    
    public void reset() {
    	
    	if (getIsFixed() == false) mAngle = 0;
    	
    	mHighlight = false; 
    	mFailed = false; 
    	
    	setColor(); 
    }

	public boolean getIsFixed() {
		return mIsDrive || mIsScrew; 
	}
		
	public void setFailed(boolean value) { 
		
		mFailed = value; 
		
		setColor(); 
	}
	
	public boolean getFailed() { 
		return mFailed; 
	}

	public void setHighlight(boolean value) { 
		mHighlight = value; 
		
		setColor(); 
	}
	
	public boolean getHighlight() { 
		return mHighlight; 
	}
	
    public float getRadius() { 
    	return (mSprite.getWidth() * 0.5f) - 3f;   	
    }
    
    public float getRadiusIncludingTeeth() { 
    	return getRadius() + 4f;   	
    }    
    
    public float getCenterX() {
    	return  mSprite.getX() + (mSprite.getWidth() * 0.5f);   	
    }

    public void setCenterX(float value) {
    	mSprite.setX(value - (mSprite.getWidth() * 0.5f));   	
    }

    public float getCenterY() {
    	return  mSprite.getY() + (mSprite.getHeight() * 0.5f);   	
    }
    
    public void setCenterY(float value) {
    	mSprite.setY(value - (mSprite.getHeight() * 0.5f));   	
    }
    
    
    public void draw(SpriteBatch sb, boolean animateJam, boolean jammedDir) {
    	
    	if (animateJam == true && mVisited == true) { 
    		mSprite.setRotation(mAngle + (jammedDir == mDir ? 2 : -2));
    	}
    	else { 
    		mSprite.setRotation(mAngle);
    	}
    	
        mSprite.draw(sb);
    }
    
    public void draw(SpriteBatch sb) {
        mSprite.setRotation(mAngle);
        mSprite.draw(sb);
    }
    
    public void fixToGrid() { 
    	int x = (int)getCenterX(); 
    	int y = (int)getCenterY();
    	
    	x -= x % 2; // 8; 
    	y -= y % 2; // 8;
    	
    	setCenterX((float)x);
    	setCenterY((float)y); 
    }

    public void update() {
        if (mMouseTracking) {
            float x = InputHandler.getScreenX();
            float y = InputHandler.getScreenY();
            
            setCenterX(x); 
            setCenterY(y); 
            // mSprite.setPosition(x, y);
        }
        
        //if (mIsDrive) {
        //    mAngle += 1;
        //}
    }

    public void setMouseTracking(boolean b) {
        mMouseTracking = b;
        
        if (b == false) mCanPlace  = false; 
        
        setColor(); 
    }
    
    private void setColor() { 
    	if (mFailed == true) mSprite.setColor(0.65f, 0.3f, 0.3f, mMouseTracking && mCanPlace == false ? 0.5f : 1f);
    	else if (mHighlight == true) mSprite.setColor(0.3f, 0.65f, 0.3f, mMouseTracking && mCanPlace == false ? 0.5f : 1f);
		else  mSprite.setColor(1f, 1f, 1f, mMouseTracking && mCanPlace == false ? 0.5f : 1f);
    }
    
    public void promoteToDrive() {
        mIsDrive = true;
    }
    
	public void promoteToScrew() {
		mIsScrew = true;	
	}

	public void rotate(boolean dir) {
		float angleSpeed = 360f / GameLogic.COG_MOVE_FRAMES; 
		
		mNextAngle = mAngle + (dir ? -angleSpeed : angleSpeed); 	
	}
	
	public void applyRotation() { 
		mAngle = mNextAngle; 
	}

	public boolean isTouchOn(int px, int py) {
		float x = getCenterX() - (float)px;
		float y = getCenterY() - (float)py;
		
		double dist = Math.sqrt(x * x + y * y);
		
		return dist <= getRadius(); 	
	}
	
	public boolean isPossibleOverlapping(Cog other) {
		float x = getCenterX() - other.getCenterX();
		float y = getCenterY() - other.getCenterY();
		
		double dist = Math.sqrt(x * x + y * y);
		
		return dist <= getRadiusIncludingTeeth() + other.getRadiusIncludingTeeth(); 	
	}
	
	public boolean isOverlapping(Cog other) {
		float x = getCenterX() - other.getCenterX();
		float y = getCenterY() - other.getCenterY();
		
		double dist = Math.sqrt(x * x + y * y);
		
		return dist <= getRadius() + other.getRadius(); 	
	}

	public void setCanPlace(boolean canPlace) {
				
		if (mCanPlace != canPlace) {
		    
		    SoundSystem.playWithDelay("canplace2", 500);
		}
		
		mCanPlace = canPlace;
		
		setColor(); 
		// mSprite.setColor(1, 1, 1, mCanPlace == false ? 0.5f : 1f); 
	}
}
