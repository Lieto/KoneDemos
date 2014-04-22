package ballisticdemo;

import java.awt.Font;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

//import org.newdawn.slick.TrueTypeFont;


public class BallisticDemo {
	
	private static int ammoRounds = 16;
	
	AmmoRound[] ammo; 
	
	public ShotType currentShotType;
	
	long lastFPS;
	
	//protected  TrueTypeFont font;
	
	public BallisticDemo()
	{
		ammo = new AmmoRound[ammoRounds];
		
		currentShotType = ShotType.LASER;
		
		for (int i = 0; i < ammoRounds; i++)
		{
			ammo[i] = new AmmoRound();
			ammo[i].type = ShotType.UNUSED;
			
		}
		
		//Font awtFont = new Font("Verdana", Font.BOLD, 12);
		//font = new TrueTypeFont(awtFont, antiAlias);
		
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
	

	public static void Render(GL2 gl2, int width, int height) {
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
		
	/*	
		for (int i = 0; i < ammoRounds; i++)
		{
			if (ammo[i].type != ShotType.UNUSED)
			{
				ammo[i].Render(gl2);
			}
			
		}
		*/
		//GL11.glColor3f(0.0f, 0.0f, 0.0f);
		//RenderText(10.0f, 24.0f, "Click: Fire \n 1-4: Select Ammo", font);
		
		/*
		switch(currentShotType)
		{
		case PISTOL: RenderText(10.0f, 10.0f, "Current Ammo: Pistol", font); break;
		case ARTILLERY: RenderText(10.0f, 10.0f, "Current Ammo: Artillery", font); break;
		case FIREBALL: RenderText(10.0f, 10.0f, "Current Ammo: Fireball", font); break;
		case LASER: RenderText(10.0f, 10.0f, "Current Ammo: Laser", font); break;
		
		
		}
		*/
	
		
		
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

	public static void Setup(GL2 gl2, int width, int height) {
		// TODO Auto-generated method stub
				gl2.glClearColor(0.9f, 0.95f, 1.0f, 1.0f);
				
				gl2.glEnable(GL2.GL_DEPTH_TEST);
				gl2.glShadeModel(GL2.GL_SMOOTH);
			
				gl2.glMatrixMode(GL2.GL_PROJECTION);
				gl2.glLoadIdentity();
			
				GLU glu = new GLU();
				//GLU.gluPerspective(50.0f,  ((float)width/(float)height), 3.0f, 7.0f);
				
				glu.gluPerspective(60.0f,  ((float)width/(float)height), 1.0f, 500.0f);
				gl2.glMatrixMode(GL2.GL_MODELVIEW);
		
	}

}
