package snakegame;

import java.awt.*;//for color class
import java.awt.event.*;//for action class
import javax.swing.*;

public class Board extends JPanel implements ActionListener{//actionlistener is interface to capture actions

    private Image apple;
    private Image snakeface;
    private Image body;

    private final int ALL_DOTS=900;//FRAME KI SIZE HAI KYUKI ARRAY MEIN SIZE DENA PADHTA HAI
    private final int DOT_SIZE=10;//DOT KI WIDTH FOR HOW TO PLACE IMAGES
    private final int RANDOM_POSITION=29;//to generate apple in random position

    private int apple_x;
    private int apple_y;

    private final int x[]=new int[ALL_DOTS];
    private final int y[]=new int[ALL_DOTS];

    //for direction of snake
    private boolean leftdirection=false;
    private boolean rightdirection=true;
    private boolean updirection=false;
    private boolean downdirection=false;

    private boolean ingame=true;


    private int dots;
    private Timer timer;//to make game functional,snake badhana

    Board(){//not component of swing
        addKeyListener(new TAdapter());//object

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(300,300));
        setFocusable(true);
        // make sure this panel has focus so key events are received
        requestFocusInWindow();

        loadImages();//function for images
        initGame();//function to start game 
    }
    public void loadImages(){
        ImageIcon i1=new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/apple.png"));//object
        apple=i1.getImage();//store

        ImageIcon i2=new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/snakeface.png"));//object
        snakeface=i2.getImage();//store

        ImageIcon i3=new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/body.png"));//object
        body=i3.getImage();//store  
    }
    public void initGame(){
    dots=3;

        for(int i=0;i<dots;i++){
            y[i]=50;
            x[i]=50-i*DOT_SIZE;

        }

        locateApple();//function to generate apple

        timer=new Timer(140,this);//call actionperformed
        timer.start();
    }

    public void locateApple(){
        //x axis
        int r=(int)(Math.random()*RANDOM_POSITION);
        apple_x=r*DOT_SIZE;

        //y axis
        r=(int)(Math.random()*RANDOM_POSITION);
        apple_y=r*DOT_SIZE;
    }
//to display image on frame
    public void paintComponent(Graphics g){//method
        super.paintComponent(g);//parent call

        draw(g);
    }
    public void draw (Graphics g){
        if(ingame){
            g.drawImage(apple, apple_x, apple_y, DOT_SIZE, DOT_SIZE, this);//generate apple (size adjusted)
            for(int i=0;i<dots;i++){
                if(i==0){
                    g.drawImage(snakeface, x[i], y[i], DOT_SIZE, DOT_SIZE, this);//generate face
                }
                else{
                    g.drawImage(body, x[i], y[i], DOT_SIZE, DOT_SIZE, this);//generate body
                }
            }
            Toolkit.getDefaultToolkit().sync();//to initialize by default
        }
        else{
            gameOver(g);
        }
    }
    public void gameOver(Graphics g){
        String msg="GAME OVER";
        Font font=new Font("SAN_SERIF",Font.BOLD,14);//for change of font color
        FontMetrics metrices=getFontMetrics(font);//object
        g.setColor(Color.WHITE);
        g.setFont(font);

        g.drawString(msg,(300-metrices.stringWidth(msg))/2,300/2);//metrice=font class obj
    }
    public void move(){
        for(int i=dots;i>0;i--){//for body
            x[i]=x[i-1];
            y[i]=y[i-1];
        }
        if (leftdirection){
            x[0]=x[0]-DOT_SIZE;
        }
        if (rightdirection){
            x[0]=x[0]+DOT_SIZE;
        }
        if (updirection){
            y[0]=y[0]-DOT_SIZE;
        }
        if (downdirection){
            y[0]=y[0]+DOT_SIZE;
        }

    }
    public void checkApple(){//to helpsnake apple khayega,length increase
        // use rectangle intersection so detection works even if images/sizes change
        Rectangle head = new Rectangle(x[0], y[0], DOT_SIZE, DOT_SIZE);
        Rectangle appleRect = new Rectangle(apple_x, apple_y, DOT_SIZE, DOT_SIZE);
        if (head.intersects(appleRect)) {
            System.out.println("Apple eaten at (" + apple_x + "," + apple_y + ")");
            dots++;
            locateApple();
        }
    }
    public void checkCollision(){
        // head rectangle (use DOT_SIZE as drawn size)
        Rectangle head = new Rectangle(x[0], y[0], DOT_SIZE, DOT_SIZE);

        // check collision with body segments
        for (int i = 1; i < dots; i++) {
            // optionally skip the first few segments to avoid false positives when snake is short
            if (i <= 4) continue;
            Rectangle part = new Rectangle(x[i], y[i], DOT_SIZE, DOT_SIZE);
            if (head.intersects(part)) {
                System.out.println("Collision with body at segment " + i + " (" + x[i] + "," + y[i] + ")");
                ingame = false;
                timer.stop();
                return;
            }
        }

        // boundary checks (head only)
        if (x[0] >= 300 || y[0] >= 300 || x[0] < 0 || y[0] < 0) {
            System.out.println("Collision with boundary at (" + x[0] + "," + y[0] + ")");
            ingame = false;
            timer.stop();
        }
    }
    public void actionPerformed(ActionEvent ae){//to override methods for abstract class(timer)
        if(ingame){
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }
    public class TAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent e){//override
            int key=e.getKeyCode();

            if(key==KeyEvent.VK_LEFT && (!rightdirection)){
                leftdirection=true;
                rightdirection=false;
                updirection=false;
                downdirection=false;
            }
            if(key==KeyEvent.VK_RIGHT && (!leftdirection)){
                leftdirection=false;
                rightdirection=true;
                updirection=false;
                downdirection=false;
            }
            if(key==KeyEvent.VK_UP && (!downdirection)){
                leftdirection=false;
                rightdirection=false;
                updirection=true;
                downdirection=false;
            }
            if(key==KeyEvent.VK_DOWN && (!updirection)){
                leftdirection=false;
                rightdirection=false;
                updirection=false;
                downdirection=true;
            }
        }
    }
}