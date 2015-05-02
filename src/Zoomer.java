import javax.swing.*;

import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;

public class Zoomer extends JFrame {
  
  final int IMG_WIDTH = 800;
  final int IMG_HEIGHT = IMG_WIDTH;  // you can change it, but then everything's skewed to begin with.
  int IMG_iters = 100;
  Complex IMG_c = new Complex(-.62772, .42193);
  
  // lower lefthand corner of the normally-viewed complex plane.
  final Complex IMG_LOW = new Complex(-2,-2);
  // upper righthand corner of the normally-viewed complex plane.
  final Complex IMG_HIGH = new Complex(2,2);
  
  // stores either the Julia or Mandelbrot object that's being drawn.
  Fractal fract;
  // which one is it?
  boolean currentlyJulia;
  // we draw the pixels of this img directly into a JLabel.
  BufferedImage img;
  
  // some elements of the GUI.
  JButton quitButton, zoomIn, zoomOut, reset;
  JLabel viz, region, mloc;
  
  // location for start/stop location of a mouse drag.
  // needs to be saved between the two events (press/release).
  int startMouseX=0, startMouseY=0;
  int stopMouseX=0, stopMouseY=0;
  
  // 
  public static void main(String[] args){
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run(){
        Zoomer z = new Zoomer();
        z.setVisible(true);
      }
    });
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public Zoomer(){
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // start with a Mandelbrot fractal.
    currentlyJulia = false;
    if (currentlyJulia){ fract = new Julia     (IMG_LOW.copy(),IMG_HIGH.copy(),IMG_WIDTH,IMG_HEIGHT,IMG_iters, IMG_c); }
    else               { fract = new Mandelbrot(IMG_LOW.copy(),IMG_HIGH.copy(),IMG_WIDTH,IMG_HEIGHT,IMG_iters);  }
    
    // border layout. gaps are 5 in both horiz/vert directions.
    this.setLayout(new BorderLayout(5,5));
    
    // set the full screen's size.
    setTitle("Fractal Zoomer");
    setSize(800,800);
    //setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    //// CENTER PANEL : picture ////
    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.setSize(500, 700);
    this.add(centerPanel,BorderLayout.CENTER);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints cc = new GridBagConstraints();
    
    //Giant image in a label.
    JPanel mouseLocation = new JPanel();
    mouseLocation.setLayout(gbl);
    centerPanel.add(mouseLocation, cc);
    
    drawPicture();
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // the JLabel that hold the picture.
    viz = new JLabel(new ImageIcon(img));
    centerPanel.add(viz,cc);
    
    viz.addMouseListener(new MouseListener(){
      
      public void mousePressed(MouseEvent e){
        startMouseX = e.getX();
        startMouseY = fract.nrows - e.getY();
      }
      
      public void mouseReleased(MouseEvent e){
        stopMouseX = e.getX();
        stopMouseY = fract.nrows - e.getY();
        
        int newRL = startMouseX;
        int newRH = stopMouseX;
        if (newRL>newRH){
          int temp = newRL;
          newRL = newRH;
          newRH = temp;
        }
        int newIL = startMouseY;
        int newIH = stopMouseY;
        if(newIL>newIH) {
          int temp = newIL;
          newIL = newIH;
          newIH = temp;
        }
        
        double low_r  = fract.low.r + (((double)newRL)/(fract.ncols-1))*(fract.high.r - fract.low.r);
        double high_r = fract.low.r + (((double)newRH)/(fract.ncols-1))*(fract.high.r - fract.low.r);
        double low_i  = fract.low.i + (((double)newIL)/(fract.nrows-1))*(fract.high.i - fract.low.i);
        double high_i = fract.low.i + (((double)newIH)/(fract.nrows-1))*(fract.high.i - fract.low.i);
        
        Complex newLow = new Complex(low_r, low_i);
        Complex newHigh = new Complex(high_r, high_i);
        fract.updateDimensions(newLow, newHigh);
        
        region.setText(regionString(fract));
        //redraw image.
        drawPicture();
        viz.setIcon(new ImageIcon(img));
      }
      
      @Override public void mouseExited(MouseEvent e){}
      @Override public void mouseEntered(MouseEvent e){}
      @Override public void mouseClicked(MouseEvent e){}
    });
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    //// RIGHT  PANEL : buttons in a column. ////
    
    JPanel rightPanel = new JPanel(new GridBagLayout());
    GridBagConstraints rc = new GridBagConstraints();
    rightPanel.setLayout(gbl);
    this.add(rightPanel,BorderLayout.LINE_END);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Quit button.
    
    quitButton = new JButton("quit");
    quitButton.setToolTipText("leave the program.");
    quitButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent event) {
        System.exit(0);
      }
    });
    rc.gridx=0;
    rc.gridy=0;
    rightPanel.add(quitButton,rc);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Reset button.
    
    reset = new JButton("Reset");
    reset.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent event) {
        fract.updateDimensions(IMG_LOW.copy(), IMG_HIGH.copy());
        //redraw image.
        redrawPicture();
      }
    });
    rc.gridy++;
    rightPanel.add(reset,rc);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Zoom-In button.
    
    zoomIn = new JButton(">> IN <<");
    zoomIn.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent event) {
        fract.zoom(2);
        //redraw image.
        redrawPicture();
      }
    });
    rc.gridy++;
    rightPanel.add(zoomIn,rc);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Zoom-Out button.
    
    zoomOut = new JButton("<< OUT >>");
    zoomOut.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent event) {
        fract.zoom(0.5);
        redrawPicture();
      }
    });
    rc.gridy++;
    rightPanel.add(zoomOut,rc);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Iterations label/textfield
    
    JLabel itersLabel = new JLabel ("#iterations:");
    rc.gridy++;
    rightPanel.add(itersLabel,rc);
    final JTextField itersTF = new JTextField(4);
    itersTF.setText(fract.maxIters+"");
    itersTF.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent event) {
        try{
          int newmax = Integer.parseInt(itersTF.getText());
          fract.maxIters = newmax;
          fract.escapeVals = fract.escapes();
          redrawPicture();
        }
        catch (NumberFormatException e){}
      }
    });
    rc.gridy++;
    rightPanel.add(itersTF,rc);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Toggle Between Fractal Kinds.
    
    final String mandelString = "mandelbrot set";
    JRadioButton mandelbut = new JRadioButton(mandelString);
    mandelbut.setActionCommand(mandelString);
    mandelbut.setSelected(true);
    
    final String juliaString = "julia set";
    JRadioButton juliabut  = new JRadioButton(juliaString);
    juliabut.setActionCommand(juliaString);
    
    ButtonGroup group = new ButtonGroup();
    group.add(mandelbut);
    group.add(juliabut);
    
    ActionListener fractalToggler = new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent event) {
        String now = event.getActionCommand();
        if (!currentlyJulia && now.equals(juliaString)){
          fract = new Julia     (IMG_LOW,IMG_HIGH,IMG_WIDTH,IMG_HEIGHT,fract.maxIters, IMG_c);
        } else if (currentlyJulia && now.equals(mandelString)) {
          fract = new Mandelbrot(IMG_LOW,IMG_HIGH,IMG_WIDTH,IMG_HEIGHT,fract.maxIters);
        }
        currentlyJulia = ! currentlyJulia;
        itersTF.setText(fract.maxIters+"");
        //redraw image.
        redrawPicture();
      }
    };
    mandelbut.addActionListener(fractalToggler);
    juliabut.addActionListener(fractalToggler);
    
    rc.gridy++;
    rightPanel.add(mandelbut,rc);
    rc.gridy++;
    rightPanel.add(juliabut,rc);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // current region label.
    region = new JLabel("",SwingConstants.CENTER);
    region.setText(regionString(fract));
    rc.gridy++;
    this.add(region,BorderLayout.PAGE_END);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    pack();
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static int[] colors(){
    int size = 256;
    int[] c = new int[size];
    
    for (int i=0; i<c.length;i++){
      // separate parabolae for red and green in the middle...
      int r =(int)((size-1) + (-0.02 * Math.pow((i- size/3),2)));
      int g =(int)((size-1) + (-0.03 * Math.pow((i-((2*size)/3)),2)));
      // A blue parabola at both ends.
      int b1=(int)((size-1) + (-0.05 * Math.pow((i-  0),2)));
      int b2=(int)((size-1) + (-0.05 * Math.pow((i-size),2)));
      int b = Math.max(Math.max(b1,b2),0);
      
      // don't let any of them go negative.
      r = r<0 ? 0 : r;
      g = g<0 ? 0 : g;
      b = b<0 ? 0 : b;
      
      // store the final color as an RGB int.
      c[i]=makeRGB(r,g,b);
    }
    return c;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  // performs the packing and unpacking between an RGB int and
  // individual color codes.
  public static int makeRGB(int r, int g, int b){ return (r<<16) + (g<<8) + b; }
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  
  //updates the img variable, but we still need to put this new version into the viz JLabel again. Not done here.
  public void drawPicture(){
    int WIDTH=fract.ncols;
    int HEIGHT=fract.nrows;
    int[] pallette = colors();
    int offset = 100;
    int newrgb;
    int escape;
    final int BLACK = makeRGB(0,0,0);
    img = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
    for (int i=0; i<fract.escapeVals.length; i++){
      for (int j=0; j<fract.escapeVals[i].length; j++){
        escape = fract.escapeVals[i][j];
        newrgb = (escape+offset)%pallette.length;
        newrgb = escape==fract.maxIters ? BLACK : pallette[newrgb];
        img.setRGB(j,i,newrgb);
      }
    }    
  }
  
  public void redrawPicture(){
        region.setText(regionString(fract));
        drawPicture();
        viz.setIcon(new ImageIcon(img));
  }
  
  // should have been in Fractal, but didn't want to change the spec.
  public String regionString(Fractal f){
          return (String.format("region: (%5g+%5g) to (%5g+%5g). c=%s", f.low.r, f.low.i, f.high.r, f.high.i, f.c));
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  
}