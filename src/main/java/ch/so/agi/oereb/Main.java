package ch.so.agi.oereb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.geotools.data.ows.HTTPClient;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.WMSUtils;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.map.WMSLayer;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.ScrollWheelTool;
import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.SAXException;

import com.formdev.flatlaf.FlatLightLaf;

public class Main extends JFrame {
    
    public Main() {
        FlatLightLaf.install();
        try {
            UIManager.setLookAndFeel(com.formdev.flatlaf.FlatIntelliJLaf.class.getName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initUI();
    }
    
    private void initUI() {     
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener((event) -> System.exit(0));

        JButton fooButton = new JButton("Foo");
        fooButton.addActionListener((event) -> System.exit(0));

                
        URL url = null;
        try {
//            url = new URL("https://geo.so.ch/wms/oereb?REQUEST=GetCapabilities&SERVICE=WMS&VERSION=1.3.0");
            url = new URL("https://geo.so.ch/api/wms?SERVICE=WMS&REQUEST=GetCapabilities&VERSION=1.3.0");
        } catch (MalformedURLException e) {
          //will not happen
        }

        WebMapServer wms = null;
        try {
          wms = new WebMapServer(url);
        } catch (IOException e) {
          //There was an error communicating with the server
          //For example, the server is down
        } catch (ServiceException e) {
          //The server returned a ServiceException (unusual in this case)
        } catch (SAXException e) {
          //Unable to parse the response from the server
          //For example, the capabilities it returned was not valid
        }
        
        HTTPClient httpClient = wms.getHTTPClient();

        WMSCapabilities capabilities = wms.getCapabilities();

        String serverName = capabilities.getService().getName();
        String serverTitle = capabilities.getService().getTitle();
        System.out.println("Capabilities retrieved from server: " + serverName + " (" + serverTitle + ")");

        Layer[] layers = WMSUtils.getNamedLayers(capabilities);
        Layer myLayer = null;
        for (Layer layer : layers) {
            System.out.println(layer.getName());
            
            //if (layer.getName().equalsIgnoreCase("ch.SO.NutzungsplanungGrundnutzung")) {
            if (layer.getName().equalsIgnoreCase("ch.so.agi.hintergrundkarte_farbig")) {
                myLayer = layer;
            }
        }
        
        MapContent map = new MapContent();
        MapViewport vp = new MapViewport();
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode("EPSG:2056");
            vp.setCoordinateReferenceSystem(crs);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        Envelope envelope = new Envelope(2599220, 2639190, 1214710, 1259860);
        ReferencedEnvelope re = new ReferencedEnvelope(envelope, crs);
        vp.setBounds(re);
//        map.setViewport(vp);
               
        map.addLayer(new WMSLayer(wms, myLayer));
        
        GTRenderer renderer = new StreamingRenderer();
        JMapPane mapPane = new JMapPane(map);
        mapPane.setRenderer(renderer);
        mapPane.setDisplayArea(re);
//        mapPane.setm.setMapArea(map.getMaxBounds());
        
        mapPane.addMouseListener(new ScrollWheelTool(mapPane));
        
        mapPane.addMouseListener(
                new MapMouseAdapter() {

                    private Point panePos;
                    boolean panning = false;
                    
                    @Override
                    public void onMouseClicked(MapMouseEvent ev) {
                        // print the screen and world position of the mouse
                        System.out.println("mouse click at");
                        System.out.printf("  screen: x=%d y=%d \n", ev.getX(), ev.getY());

                        DirectPosition2D pos = ev.getWorldPos();
                        System.out.printf("  world: x=%.2f y=%.2f \n", pos.x, pos.y);
                    }

                    @Override
                    public void onMouseEntered(MapMouseEvent ev) {
                        System.out.println("mouse entered map pane");
                    }

                    @Override
                    public void onMouseExited(MapMouseEvent ev) {
                        System.out.println("mouse left map pane");
                    }

                    @Override
                    public void onMousePressed(MapMouseEvent ev) {
                        System.out.println("onMousePressed");
                        panePos = ev.getPoint();
                        panning = true;
                    }
                    
                    @Override
                    public void onMouseDragged(MapMouseEvent ev) {
                        System.out.println("onMouseDragged");
                        if (panning) {
                            Point pos = ev.getPoint();
                            if (!pos.equals(panePos)) {
                                mapPane.moveImage(pos.x - panePos.x, pos.y - panePos.y);
                                panePos = pos;
                            }
                        }
                    }
                    
                    @Override
                    public void onMouseReleased(MapMouseEvent ev) {
                        System.out.println("onMouseReleased");
                        panning = false;
                    }
                    
                });

        
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();

        topPanel.setBackground(Color.GRAY);
        topPanel.setPreferredSize(new Dimension(250, 150));
//        bottomPanel.add(topPanel);
        bottomPanel.add(mapPane);

        bottomPanel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));

        add(bottomPanel);

        pack();

        
        
//        createLayout(quitButton, fooButton, mapPane);
        

        setTitle("Quit button");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


    }
    
    private void createLayout(JComponent... arg) {
        
        add(arg[2]);

//        Container pane = getContentPane();
//        GroupLayout gl = new GroupLayout(pane);
//        pane.setLayout(gl);
//        
//        gl.setAutoCreateContainerGaps(true);
//        ParallelGroup hsg = gl.createParallelGroup();
//        hsg.addComponent(arg[2]);
        
//        hsg.addComponent(arg[0]).addComponent(arg[1]);
//        hsg.addComponent(arg[1]);
        
//        SequentialGroup vsg = gl.createSequentialGroup();
//      vsg.addComponent(arg[0]);
//      vsg.addComponent(arg[1]);
//        vsg.addComponent(arg[2]);

//        gl.setHorizontalGroup(hsg);
//        gl.setVerticalGroup(vsg);
    }
    

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {

            Main app = new Main();
            app.setVisible(true);
        });
    }
}
