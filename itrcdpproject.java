import java.awt.event.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

import javax.swing.*;

import com.genlogic.*;



public class itrcdpproject extends GlgJBean implements ActionListener 
{

    static final long serialVersionUID = 0;

    static final int NUM_VALUES = 3;
    boolean PerformUpdates = true;

    GlgAnimationValue [] animation_array = new GlgAnimationValue[ NUM_VALUES ];
    static boolean AntiAliasing = true;

    Timer timer = null;

    public itrcdpproject()
    {
       super();
       SetDResource( "$config/GlgAntiAliasing", AntiAliasing ? 1.0 : 0.0 );
    }
 
    public void ReadyCallback( GlgObject viewport ) 
   {
      super.ReadyCallback( viewport );

      InitializeArrays();
      
      if( timer == null )
      {
        timer = new Timer( 350, this );
        timer.setRepeats( false );
        timer.start();
     }
  }


    public static void main( final String [] arg )
   {
      SwingUtilities.
        invokeLater( new Runnable() { public void run() { Main( arg ); } } );
   }
    public static void Main( final String[] args) 
    {

        class DemoQuit extends WindowAdapter
      {
         public void windowClosing( WindowEvent e ) { System.exit( 0 ); }
      } 

       
      JFrame frame = new JFrame( "ITR CDP Project" );

      frame.setResizable( true );
      frame.setSize( 750, 600 );
      frame.setLocation( 20, 20 );

      itrcdpproject controls = new itrcdpproject();
      frame.getContentPane().add( controls );

      frame.addWindowListener( new DemoQuit() );
      frame.setVisible( true );

      controls.SetDrawingName( "meter.g" );
    }
    
 int vel=0,pre=0,vol=0;
    public void UpdateMeter()
   { 
      if( timer == null )
        return;   // Prevents race conditions

      if( PerformUpdates )
      {
         
          
                        try
                        {
                           
                           DatagramSocket ds=new DatagramSocket(9999);

                           byte[] packet=new byte[1024];

                           DatagramPacket dp=new DatagramPacket(packet,1024);
                           ds.receive(dp);

                           String data=new String(dp.getData(), 0,dp.getLength());

                           Scanner sc=new Scanner(data);

                           while(sc.hasNextInt())
                           {
                              vel=sc.nextInt();
                              pre=sc.nextInt();
                              vol=sc.nextInt();
                           }
                        
                                    SetDResource("vel/Value",vel);
                                    
                                    SetDResource("pre/Value",pre);

                                    SetDResource("vol/Value",vol);
                                   
                                    animation_array[ 0 ] = new GlgAnimationValue( this,GlgAnimationValue.SIN,0, 2 ,vel ,vel, "velgraph/Chart/Plots/Plot#0/ValueEntryPoint" );
                                    animation_array[ 1 ] = new GlgAnimationValue( this,GlgAnimationValue.SIN,0, 2 ,pre ,pre, "pregraph/Chart/Plots/Plot#0/ValueEntryPoint" );
                                    animation_array[ 2 ] = new GlgAnimationValue( this,GlgAnimationValue.SIN,0, 2 ,vol ,vol, "volgraph/Chart/Plots/Plot#0/ValueEntryPoint" );
                                    animation_array[ 0 ].Iterate();
                                    animation_array[ 1 ].Iterate();
                                    animation_array[ 2 ].Iterate();
                                                                     
                                    Update();
                                  ds.close();

                           }

                       
                        catch(Exception e)
                        {
                           System.out.println(e);
                        }
      }

      timer.start();   // Restart the update timer
      if( !timer.isRunning() )
        timer.start();
   }

/////////////////////////////////////////////////////
   public void StopUpdates()
   {
      if( timer != null )
      {
         timer.stop();
         timer = null;
      }
   }
//////////////////////////////////////////////////////

   public void Start()
   {
      PerformUpdates = true;
      if( timer != null )
        timer.start();
   }
   ///////////////////////////////////////////////////
    public void Stop()
   {      
      PerformUpdates = false;
      if( timer != null )
        timer.stop();
   }

   
   //////////////////////////////////////////////////////////////////////////
   public void ToggleAntiAliasing()
   {
      AntiAliasing = !AntiAliasing;
      SetDResource( "$config/GlgAntiAliasing", AntiAliasing ? 1.0 : 0.0 );

      // Restart with new AntiAliasing setting
      stop();
      start();
   }

   //////////////////////////////////////////////////////////////////////////
   // ActionListener method to use the bean as update timer's ActionListener.
   //////////////////////////////////////////////////////////////////////////
   public void actionPerformed( ActionEvent e )
   {
      UpdateMeter();
   }
   //////////////////////////////////////////



   void InitializeArrays()
   {
      
   }
}

