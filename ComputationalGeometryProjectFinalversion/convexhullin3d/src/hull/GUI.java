package hull;
import j3d.*;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;


public class GUI extends Applet implements
 MouseListener, MouseMotionListener, WindowListener,ActionListener{
   public ConvexHull hull;       // Implements the algorithm
   public Canvas3D canvas;       // Renders the 3D convex hull
   public MouseMove moves;       // The control buttons
   public Image graphic;       // for double buffering
   public boolean mousedown;     // current mouse state
   public Matrix rotation;       
   public int width;
   public int height;
   public static int t1 = 0;
   public static int t2 = 0;
   public static int k = 1;
   public long a = 0;
   public long b = 0;
   public GUI() { }
   Button bt,bt1,bt2,bt3,bt4,bt5,bt6;

   public void init(){ 
      this.hull = new ConvexHull();
      this.canvas = new Canvas3D();
     // this.offscreen = null;
      this.mousedown = false;
      this.moves = new MouseMove();
      bt = new Button("ReStart 100");
      bt1 = new Button("ReStart");
      bt2 = new Button("Add 100 Points");
      bt3 = new Button("Increment");
   //   bt4 = new Button("ShowDemo");
      bt4 = new Button("Increment Fill All");
      bt5 = new Button("Brute Force");
      bt6 = new Button("Brute Force Fill All");
    //  bt1 = new Button("ReStart 100");
      bt.addActionListener(this);
      bt1.addActionListener(this);
      bt2.addActionListener(this);
      bt3.addActionListener(this);
      bt4.addActionListener(this);
      bt5.addActionListener(this);
      bt6.addActionListener(this);
      add(bt,BorderLayout.SOUTH);
      add(bt1,BorderLayout.SOUTH);
      add(bt2,BorderLayout.SOUTH);
      add(bt3,BorderLayout.SOUTH);
      add(bt4,BorderLayout.SOUTH);
      add(bt5,BorderLayout.SOUTH);
      add(bt6,BorderLayout.SOUTH);
      this.rotation = new Matrix();
      setBackground(null);
      addMouseListener(this);
      addMouseMotionListener(this);
   }

   public void actionPerformed(ActionEvent ev){
      if(ev.getSource().equals(bt3)){  //start
    	  t1 = 0; t2 = 0;
    	  a = System.currentTimeMillis();
    	  this.hull.start(k);
    	  b = System.currentTimeMillis() - a;
      }
      else if(ev.getSource().equals(bt)){ //restart 100
    	  t1 = 0; t2 = 0;
    	  a = 0;   b = 0;
    	 // a = System.currentTimeMillis();
    	  k = 1;
    	  this.hull.restart(k);
    	//  b = System.currentTimeMillis() - a;
      }
      else if(ev.getSource().equals(bt1)){   //restart
    	  t1 = 0; t2 = 0;
    	  a = 0;   b = 0;
    	  this.hull.restart(k);
    	 // b = System.currentTimeMillis() - a;
      }
      else if(ev.getSource().equals(bt2)){    //add 100
    	  t1 = 0; t2 = 0;
    	 // a = System.currentTimeMillis();
    	  a = 0;   b = 0;
    	  k++;
    	  this.hull.restart(k);
    	 // b = System.currentTimeMillis() - a;
      }
      else if(ev.getSource().equals(bt4)){   //fill all
    	  t1 = 0; t2 = 0;
    	  a = System.currentTimeMillis();
    	  this.hull.fillall(k);
    	  b = System.currentTimeMillis() - a;
      }
      else if(ev.getSource().equals(bt5)){   //brute force
    	 // a = 0; b =0;
    	  a = System.currentTimeMillis();
    		  this.hull.bruteforce(false,k);
    	  b = System.currentTimeMillis() - a;
      }
      else if(ev.getSource().equals(bt6)){   //fill all
    //	 
    		  a = System.currentTimeMillis();
    		  this.hull.bruteforce(true,k);
    		  
    	  b = System.currentTimeMillis() - a;
      }
      repaint();
   }

   public void paint(Graphics g){ update(g); setSize(900,800);}
   
   public void update(Graphics graphics){
	   graphic = createImage(getWidth(), getHeight());
      Graphics g = graphic.getGraphics();
      moves.setWidth(getWidth());
      width = getWidth();
      height = getHeight();
      g.clearRect(0, 0, getWidth(), getHeight());
      g.drawString("Current Points:"+k*100, 10, 55);
      if(b==0){
    	  g.drawString("Create Points,No running time", 10, 70);
      }
      else{
      g.drawString("Running time:"+b, 10, 70);
      }
      float w = (width < height)? width : height;
      float scale = 0.65F * w / hull.getDiameter();
      Matrix m = this.hull.getMatrix();
      m.identity();
      m.mult(rotation);
      m.scale(scale, -scale, scale);
      m.translate(getWidth() / 2.0F, getHeight() / 2.0F, 0);
      this.canvas.render(hull, g);
      this.moves.draw(g);    
      graphics.drawImage(graphic, 0, 0, null);
   }
   // Spin the convex hull
   int prevx;
   int prevy; 
   public void mousePressed(MouseEvent e){
      int x = e.getX();
      int y = e.getY();
      if(moves.mouseDown(x, y)){
         repaint();
      }
      else if(!moves.intersects(e.getX(), e.getY())){
         prevx = x;
         prevy = y;
         mousedown = true;
         this.canvas.setPickPoint(x, y);
         repaint();
      }
   }

   public void mouseReleased(MouseEvent e){
      mousedown = false;
      this.canvas.setPickPoint(-1, -1);
      if(moves.mouseUp(e.getX(), e.getY())){
         repaint();
      }
   }

   public void mouseMoved(MouseEvent e){
      if(moves.mouseMove(e.getX(), e.getY())){
         repaint();
      }
   }

   public void mouseDragged(MouseEvent e){
      int x = e.getX();
      int y = e.getY();
      if (mousedown){ 
         float xtheta = ((float)(y - prevy) * 360F) / getWidth();
         float ytheta = ((float)(prevx - x) * 360F) / getHeight();    
         this.canvas.setPickPoint(x, y);
         this.rotation.rotate(xtheta, ytheta, 0F);
         repaint();  
         prevx = x;
         prevy = y;
      }
   }
   
   public void mouseClicked(MouseEvent e){ }
   public void mouseEntered(MouseEvent e){ }
   public void mouseExited(MouseEvent e){ }
   public void windowActivated(WindowEvent e){ }
   public void windowClosed(WindowEvent e){ }
   public void windowClosing(WindowEvent e){ System.exit(0); }
   public void windowDeactivated(WindowEvent e){ }
   public void windowDeiconified(WindowEvent e){ }
   public void windowIconified(WindowEvent e){ }
   public void windowOpened(WindowEvent e){ }
   
   public static void main(String args[]){
      GUI v = new GUI();
    // v.setSize(800, 800);
      v.init();
   }
}

class MouseMove{	   
	   protected int x;
	   protected int y;
	   protected int width;
	   protected int height;
	   
	   public MouseMove(){     
	   }

	   public int getWidth(){
	      return this.width;
	   }

	   public void setWidth(int width){
	      this.width = width;
	   }
	   
	   public void draw(Graphics g){   
	   }
	   
	   public boolean intersects(int x, int y){ 
	      return (x >= this.x && y >= this.y && x <= (this.x + this.width) && y <= (this.y + this.height));
	   }
	   
	   public boolean mouseDown(int x, int y){
	      return false;
	   }
	   
	   public boolean mouseUp(int x, int y){ 
	      boolean refresh = false;
	      return refresh;
	   }
	   
	   public boolean mouseMove(int x, int y){
	      boolean refresh = false;  
	      return refresh;
	   }
	}



