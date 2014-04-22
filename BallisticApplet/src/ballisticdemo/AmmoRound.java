
package ballisticdemo;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import kone.core.Particle;



public class AmmoRound 
{
	public Particle particle;
	public ShotType type;
	public long startTime;

	public AmmoRound()
	{
		particle = new Particle();
		 
	}
	
	public AmmoRound(Particle particle, ShotType type, long startTime)
	{
		this.particle = particle;
		this.type = type;
		this.startTime = startTime;
	}
	

	public void Render(GL2 gl2)
	{
		
		gl2.glColor3f(0, 0, 0);
		gl2.glPushMatrix();
		gl2.glTranslatef(particle.position.x, particle.position.y, particle.position.z);
		
		GLU glu = new GLU();
		
		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
		glu.gluQuadricNormals(sphere, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sphere,  GLU.GLU_OUTSIDE);
		glu.gluSphere(sphere, 0.3f, 5, 4);
		//Sphere sphere = new Sphere();
		
		//sphere.draw(0.3f,  5,  4);
		gl2.glPopMatrix();
		
		gl2.glColor3f(0.75f, 0.75f, 0.75f);
		gl2.glPushMatrix();
		gl2.glTranslatef(particle.position.x, 0.0f, particle.position.z);
		gl2.glScalef(1.0f,  0.1f,  1.0f);
		
		glu.gluSphere(sphere, 0.6f, 5, 4);
		//sphere.draw(0.6f,  5, 4);
		gl2.glPopMatrix();
		
	}
}
