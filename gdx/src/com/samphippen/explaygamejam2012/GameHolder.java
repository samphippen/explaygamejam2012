package com.samphippen.explaygamejam2012;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class GameHolder implements ApplicationListener {

    private SpriteBatch mSpriteBatch;
    private ShapeRenderer mDebugShapeRenderer;
    
    private Camera mCamera;
    private Vector2 mCameraOrigin = new Vector2(0, 0);    
    private Tray mTray;
    private boolean mHoldingCog = false;
    private Cog mHeldCog;
    private Cog mLastCog;
    private int mCogTime;
    private CogGraph mGraph;
	private boolean mDebugging = true;
    private boolean mRunTurns = true; 
    private GameLogic mLogic = new GameLogic();
    private Sprite mRackSprite = new Sprite();
    
    @Override
    public void create() {
    	ResourceManager.loadResources();   
       
    	mTray = new Tray();
        mGraph = new CogGraph(); 
        mRackSprite = new Sprite();
        
        
        mSpriteBatch = new SpriteBatch();
        mSpriteBatch.enableBlending();         
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        mCamera = new OrthographicCamera(800, 1280);
        mCameraOrigin.set(400, 1280/2);
          
        mDebugShapeRenderer = new ShapeRenderer(); 
        
        mLastCog = mGraph.mDrive;
                
        //createTestGraph(); 
        //createTestGraph2();
        
        mLogic.newGame(); 
    }

    private void createTestGraph() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
    	Cog cog1 = mTray.getCog();
    	cog1.setCenterX(w * 0.5f);     	
    	cog1.setCenterY(h - 80);
    	
    	Cog cog2 = mTray.getCog();
    	cog2.setCenterX(w * 0.5f);     	
    	cog2.setCenterY(h - 160);    	
    	
    	mGraph.addCog(cog1);
    	mGraph.addCog(cog2);
    	
    	mGraph.add(cog1, cog2);
    	
    	mGraph.evaluate(); 
    	
    	mHeldCog = mTray.getCog();
    	mGraph.addCog(mHeldCog);
    	
    	mHeldCog.setCenterX(w * 0.5f);  
    	mHeldCog.setCenterY(h - 240);  
    	
        mHeldCog.fixToGrid();
        if (mGraph.dropCog(mHeldCog) == false) {
        	mGraph.removeCog(mHeldCog); 
        	mTray.addCog(mHeldCog); 
        }
        
        //mGraph.removeCog(mHeldCog); 
	}    
    
    private void createTestGraph2() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
    	Cog cog1 = mTray.getCog();
    	cog1.setCenterX(w * 0.5f);     	
    	cog1.setCenterY(h - 64);
    	
    	Cog cog2 = mTray.getCog();
    	cog2.setCenterX(w * 0.5f);     	
    	cog2.setCenterY(h - 128);    	

    	Cog cog3 = mTray.getCog();
    	cog3.setCenterX(w * 0.5f);     	
    	cog3.setCenterY(h - 192);    	

    	Cog cog4 = mTray.getCog();
    	cog4.setCenterX(w * 0.5f);     	
    	cog4.setCenterY(h - 256);    	
    	
    	mGraph.addCog(cog1);
    	mGraph.addCog(cog2);
    	mGraph.addCog(cog3);
    	mGraph.addCog(cog4);
    	
    	mGraph.add(cog1, cog2);
    	mGraph.add(cog2, cog3);
    	mGraph.add(cog3, cog4);    	
    	
    	mGraph.evaluate(); 
    	
    	mHeldCog = mTray.getCog();
    	mGraph.addCog(mHeldCog);
    	
    	mHeldCog.setCenterX(w * 0.5f);  
    	mHeldCog.setCenterY(h - 320);  
    	
        mHeldCog.fixToGrid();
        if (mGraph.dropCog(mHeldCog) == false) {
        	mGraph.removeCog(mHeldCog); 
        	mTray.addCog(mHeldCog); 
        }
        
        mGraph.removeCog(mHeldCog);
        
        if (mGraph.dropCog(mHeldCog) == false) {
        	mGraph.removeCog(mHeldCog); 
        	mTray.addCog(mHeldCog); 
        }        
        
        //mGraph.removeCog(cog2);
	}
        
	@Override
    public void dispose() {
    	ResourceManager.dispose(); 
    }

    private Vector2 getCameraOrigin() {
        return mCameraOrigin;
    }

    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        Matrix4 traslate = new Matrix4().translate(-getCameraOrigin().x, -getCameraOrigin().y, 0); 
                
        mSpriteBatch.setProjectionMatrix(mCamera.combined);
        mSpriteBatch.setTransformMatrix(traslate);

        mSpriteBatch.begin();
        mTray.draw(mSpriteBatch);
        for (int i = 0; i < mGraph.mCogs.size(); i++) {
            Cog c = mGraph.mCogs.get(i);
            c.draw(mSpriteBatch);
        }

        mSpriteBatch.end();
        
        if (mDebugging  == true) {         	
        	mDebugShapeRenderer.setProjectionMatrix(mCamera.combined);
        	mDebugShapeRenderer.setTransformMatrix(traslate);
        	
        	mDebugShapeRenderer.begin(ShapeType.Line);
        	mDebugShapeRenderer.setColor(0, 1, 0, 1);
        	
        	mGraph.renderDebugLines(mDebugShapeRenderer); 
        	
        	mDebugShapeRenderer.end();
        }
    }

    public void update() {
    	
    	switch (mLogic.mState)
    	{
    		case ClearGameState:
    			mTray.addCogs(mGraph.mCogs); 
    			mGraph.clear(); 
    			mLogic.sartGame();
    			break; 
    		case GameStart:
    			// other stuff 
    			mLogic.newTurn(); 
    			break; 
    		case WaitingForPlayer:
    			doSelectingEvents(); 
    			break; 
    		case MovingCog:
    			doMovingEvents(); 
    			break; 
    		case Animating:
    			doAnimation(); 
    			break; 
    		case NextPlayer:
    			switchPlayerView();
    			break; 
    		case GameOver:
    			// other stuff 
    			mLogic.newGame(); 
    			break;     			
    		default: 
    			break; 
    		
    	}        
    	
        mCogTime += 1;

        for (int i = 0; i < mGraph.mCogs.size(); i++) {
            Cog c = mGraph.mCogs.get(i);
            c.update();
        }
    }


	private void doSelectingEvents() {
        if (!mHoldingCog && Gdx.input.isTouched()) {
            if (mTray.touchInside(Gdx.input.getX()*2, (Gdx.graphics.getHeight() - Gdx.input.getY())*2)) {
            	
            	System.out.println("Selecting cog");
            	
            	mHoldingCog = true;
                mHeldCog = mTray.getCog();
                mGraph.addCog(mHeldCog);
                mHeldCog.setMouseTracking(true);
                mCogTime = 0;
                
                mLogic.playerSelectedCog(mHeldCog, false);
            }
            else { 
            	mHeldCog = mGraph.touchOnCog(Gdx.input.getX()*2, (Gdx.graphics.getHeight() - Gdx.input.getY())*2); 
            	
            	if (mHeldCog != null) {
            		System.out.println("Picking up cog");
            		
            		mHoldingCog = true;
            		mHeldCog.setMouseTracking(true);
            		
            		System.out.println("");
            		System.out.println("");
            		
            		mLogic.playerSelectedCog(mHeldCog, true);
            	}            	
            }            	
        }
	}
	
	private void doMovingEvents() {

        if (mHoldingCog && !Gdx.input.isTouched()) {
            
        	System.out.println("Dropping cog");
        	
            mHeldCog.setMouseTracking(false);
            mHeldCog.fixToGrid();
            
            if (mGraph.dropCog(mHeldCog) == false) {
            	System.out.println("Dropping failed");
            	
            	if (mLogic.mCogWasFromBoard == false) { 
	            	mGraph.removeCog(mHeldCog); 
	            	mTray.addCog(mHeldCog); 
            	}
            	
            	mLogic.playerFailedToPlaceCog();
            }
            else {                
                mLogic.playerPlacedCog(true);
            } 
            
    		System.out.println("");
    		System.out.println("");
    		
            mLastCog = mHeldCog;  
            mHeldCog = null;
            mHoldingCog = false;
        }
	}
	
	private void doAnimation() {		
		mLogic.animationTick(); 
		mGraph.evaluate(); 		
	}

    private void switchPlayerView() {
    	mLogic.newTurn(); 
	}
	
	@Override
    public void render() {
        update();
        draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
