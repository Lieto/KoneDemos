package ballisticdemo;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;


import javax.media.opengl.*;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import com.jogamp.newt.event.KeyAdapter;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

@SuppressWarnings("serial")
public class BallisticDemoAWTApplet extends Applet implements KeyListener, MouseListener {
	
	private GLAnimatorControl glanimatorcontrol;
	
	private static int ammoRounds = 16;
	
	AmmoRound[] ammo; 
	
	public ShotType currentShotType;
	
	public BallisticDemo demo;
	
	protected TrueTypeFont font;
	protected boolean antiAlias = true;
	
	private static final int CANVAS_WIDTH = 640;
	private static final int CANVAS_HEIGHT = 480;
	
	
	public void init()
	{
		GLProfile.initSingleton();
		setLayout( new BorderLayout());
		
		ammo = new AmmoRound[ammoRounds];
		
		currentShotType = ShotType.LASER;
		
		for (int i = 0; i < ammoRounds; i++)
		{
			ammo[i] = new AmmoRound();
			ammo[i].type = ShotType.UNUSED;
			
		}
		
		final GLCanvas canvas = new GLCanvas();
		
		canvas.addMouseListener(this);
		
		canvas.addKeyListener(this);
		
		canvas.addGLEventListener(new GLEventListener()
		{
			
			@Override
			public void reshape( GLAutoDrawable glautodrawable,
					int x, int y, int width, int height) 
			{
				/*
				Setup( glautodrawable.getGL().getGL2(), 
						width, height);
						*/
				Setup( glautodrawable.getGL().getGL2(), 
						glautodrawable.getWidth(), glautodrawable.getHeight());
			}
			
			@Override
			public void init( GLAutoDrawable glautodrawable)
			{
				
				Setup( glautodrawable.getGL().getGL2(), 
						glautodrawable.getWidth(), glautodrawable.getHeight());
				
				
			}
			
			@Override
			public void dispose( GLAutoDrawable glautodrawable)
			{
				
			}
			
			@Override
			public void display( GLAutoDrawable glautodrawable)
			{
				Update((float) 1/60);
				Render( glautodrawable.getGL().getGL2(),
						glautodrawable.getWidth(), glautodrawable.getHeight());
			}
		});
		
		//canvas.setSize( getSize());
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		add(canvas, BorderLayout.CENTER);
		glanimatorcontrol = new FPSAnimator( canvas, 30);
	}
	
	public void start()
	{
		glanimatorcontrol.start();
		
	}
	
	public void stop()
	{
		glanimatorcontrol.stop();
	}
	
	public void destroy()
	{
		
	}
	
	private void Render(GL2 gl2, int width, int height) {
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl2.glLoadIdentity();
		
		GLU glu = new GLU();
		
		glu.gluLookAt(-25.0f,  8.0f,  5.0f, 
				0.0f, 5.0f,  22.0f, 
				0.0f, 1.0f, 0.0f);
		
		gl2.glColor3f(0.0f, 0.0f, 0.0f);
		gl2.glPushMatrix();
		gl2.glTranslatef(0.0f, 1.5f, 0.0f);
		
		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
		glu.gluQuadricNormals(sphere, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sphere,  GLU.GLU_OUTSIDE);
		
		glu.gluSphere(sphere, 0.1f, 5, 5);
		//Sphere sphere = new Sphere();
		//sphere.draw(0.1f, 5, 5);
		
		gl2.glTranslatef(0.0f, -1.5f, 0.0f);
		gl2.glColor3f(0.75f,  0.75f,  0.75f);
		gl2.glScalef(1.0f, 0.1f, 1.0f);
		
		glu.gluSphere(sphere, 0.1f, 5, 5);
		//sphere.draw(0.1f, 5, 5);
		gl2.glPopMatrix();
		
		gl2.glColor3f(0.75f, 0.75f, 0.75f);
		gl2.glBegin(GL2.GL_LINES);
		
		for (int i = 0; i < 200; i += 10)
		{
			gl2.glVertex3f(-5.0f, 0.0f, i);
			gl2.glVertex3f(5.0f, 0.0f, i);
		}
		
		gl2.glEnd();
		
	
		for (int i = 0; i < ammoRounds; i++)
		{
			if (ammo[i].type != ShotType.UNUSED)
			{
				ammo[i].Render(gl2);
			}
			
		}
		
		
		gl2.glColor3f(0.0f, 0.0f, 0.0f);
		RenderText(gl2, 10.0f, 24.0f, width, height,
				"Click: Fire \n 1-4: Select Ammo", font);
		
		
		switch(currentShotType)
		{
		case PISTOL: RenderText(gl2, 10.0f, 10.0f, width, height, "Current Ammo: Pistol", font); break;
		case ARTILLERY: RenderText(gl2, 10.0f, 10.0f, width, height, "Current Ammo: Artillery", font); break;
		case FIREBALL: RenderText(gl2, 10.0f, 10.0f, width, height, "Current Ammo: Fireball", font); break;
		case LASER: RenderText(gl2, 10.0f, 10.0f, width, height, "Current Ammo: Laser", font); break;
		
		
		}
		
	
		
		
	}
	
	public void Update(float duration)
	{
		if (duration <= 0.0f) return;
		
		for (int i = 0; i < ammoRounds; i++)
		{
			AmmoRound shot = ammo[i];
			
			if (shot.type != ShotType.UNUSED)
			{
				shot.particle.integrate(duration);
				
				if (shot.particle.getPosition().y < 0.0f ||
						//shot.startTime + 5000 < TimingData.getTime() ||
						shot.particle.getPosition().z > 200.0f
					)
				{
					shot.type = ShotType.UNUSED;
				}
			}
		}
		
	}
	
	public void Fire()
	{
		
		AmmoRound shot = new AmmoRound();
	
		int i;
		for (i = 0; i < ammoRounds; i++)
		{
			shot = ammo[i];
			if (shot.type == ShotType.UNUSED) break;
		}
		
		if (i > ammoRounds) return;
		
		switch (currentShotType)
		{
			case PISTOL:
				shot.particle.setMass(2.0f);
				shot.particle.setVelocity(0.0f, 0.0f, 35.0f);
				shot.particle.setAcceleration(0.0f, -1.0f, 0.0f);
				shot.particle.setDamping(0.99f);
				break;
					
			case ARTILLERY:
				shot.particle.setMass(200.0f);
				shot.particle.setVelocity(0.0f, 30.0f, 40.0f);
				shot.particle.setAcceleration(0.0f, -20.0f, 0.0f);
				shot.particle.setDamping(0.99f);
				break;
					
			case FIREBALL:
				shot.particle.setMass(1.0f);
				shot.particle.setVelocity(0.0f, 0.0f, 10.0f);
				shot.particle.setAcceleration(0.0f, 0.6f, 0.0f);
				shot.particle.setDamping(0.9f);
				break;
					
			case LASER:
				shot.particle.setMass(0.1f);
				shot.particle.setVelocity(0.0f, 0.0f, 100.0f);
				shot.particle.setAcceleration(0.0f, 0.0f, 0.0f);
				shot.particle.setDamping(0.99f);
				break;
		}
				
		shot.particle.setPosition(0.0f, 1.5f, 0.0f);
		//shot.startTime = TimingData.getTime();
		shot.type = currentShotType;
				
		shot.particle.clearAccumulator();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	
		Fire();
		System.out.println("MOUSE DOWN");
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_1: currentShotType = ShotType.PISTOL; break;
		case KeyEvent.VK_2: currentShotType = ShotType.ARTILLERY; break;
		case KeyEvent.VK_3: currentShotType = ShotType.FIREBALL; break;
		case KeyEvent.VK_4: currentShotType = ShotType.LASER; break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void RenderText(GL2 gl2, float x, float y, int width, int height, String text, TrueTypeFont font)
	{
		
		gl2.glEnable(GL2.GL_BLEND);
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
 
  
 
        gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPushMatrix();
		gl2.glLoadIdentity();
		gl2.glOrtho(0, width, height, 0, -1, 1);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPushMatrix();
		gl2.glLoadIdentity();
		
		/*
		if (font == null)
		{
			Font awtFont = new Font("Verdana", Font.BOLD, 12);
			font = new TrueTypeFont(awtFont, antiAlias);
		}
		*/
		GLUT glut = new GLUT();
		
		gl2.glRasterPos2f(x, y);
		gl2.glColor3f(0.7f, 0.0f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, text);
		//font.drawString(x, y, text, Color.red);
			
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPopMatrix();
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPopMatrix();
		
		gl2.glDisable(GL2.GL_BLEND);
		gl2.glDisable(GL2.GL_TEXTURE_2D);	
	}
	
	public void Setup(GL2 gl2, int width, int height) {
		// TODO Auto-generated method stub
				gl2.glClearColor(0.9f, 0.95f, 1.0f, 1.0f);
				
				gl2.glEnable(GL2.GL_DEPTH_TEST);
				gl2.glShadeModel(GL2.GL_SMOOTH);
			
				gl2.glMatrixMode(GL2.GL_PROJECTION);
				gl2.glLoadIdentity();
			
				GLU glu = new GLU();
				//GLU.gluPerspective(50.0f,  ((float)width/(float)height), 3.0f, 7.0f);
				
				glu.gluPerspective(90.0f,  ((float)width/(float)height), 1.0f, 500.0f);
				
				//glu.gluPerspective(45.0f,  ((float)width/(float)height), 0.1f, 100.0f);
				gl2.glMatrixMode(GL2.GL_MODELVIEW);
				
				gl2.glLoadIdentity();
		
	}
	

}
