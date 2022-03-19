/* *********************************************************************************  
 *   This file is part of GeometryPlayground.
 *
 *   GeometryPlayground is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   GeometryPlayground is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with GeometryPlayground.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;

import java.util.Locale;
import java.util.ResourceBundle;

public class GeoPlayground extends JPanel implements ActionListener,ChangeListener{
	 
	static JFrame frame; 
	private JPanel	cntrPanel;

  public static String[] parameter={"",""};
  public static String lastSaveDir="",lastOpenDir="";
  protected static JTabbedPane tabbedPanePlane;
  private JPanel[] canvasPanel;
  protected static GeoCanvas[] canvas;
  public static JRadioButton[] modelButton;
  private JPanel[] infoPanel,modelPanel;
  private JLabel[] infoLabel, modelLabel;
  public static boolean degrees=true;
  public static String commentText,cnstText,newLabelText,currentLabelText;
  public static int whatToDo=-1, model=0, digits=2;
  public static final int	MakePoints=0,   MakeMdPt=1,		MakeInt=2,
  							MakeLines=3,	MakeSegment=4,	MakePerps=5,
  							MakeBisect=6,	MakeCircles=7,  Move=8,
  							FixObject=9,	MeasureDist=10, MeasureAngle=11,
  							MeasureCirc=12, MeasureArea=13,	HideObject=14,
  							LabelObject=15,	MeasureRatio=16,MeasureSum=17,
  							MeasureDiff=18,	MeasureProd=19,
  							ReflectPt=20,	RotatePt=21,	TranslatePt=22,
  							MakeParlls=23,	MeasureTri=24,	MakeRays=25,
  							InvertPt=26,	TrackObject=27,	MakeFxPt=28,
  							MeasureCnst=29, MakeComment=30;
  static JMenuBar	theMenu;
  JMenu fileMenu,saveMenu,makeMenu,measureMenu,digitMenu,moveMenu,displayMenu,areaMenu,
  		showMenu,modelMenu,angleMenu,scaleMenu,languageMenu,helpMenu;
  JMenuItem newMI,saveMI,saveJpgMI,loadMI,quitMI,undoMI,
  			pointMI,mdPtMI,fxPtMI,intMI,
  			lineMI,segmentMI,perpMI,bisectorMI,parllMI,rayMI,
  			circleMI,
  			distanceMI,angleMI,circumMI,areaMI,triMI,
  			ratioMI,sumMI,diffMI,prodMI,cnstMI,
  			moveMI,fixMI,trackMI,
  			reflectMI,rotateMI,translateMI,invertMI,
  			labelMI,hideMI,commentMI,
  			showPtsMI,showLnsMI,showCrsMI,showMeasMI,showAllMI,
  			degreeMI,radianMI,
  			scaleUpMI,scaleDnMI,
  			zhMI,enMI,esMI,frMI,jpMI,mrMI,arMI,koMI,ruMI,huMI,itMI,ptMI,trMI,hiMI,heMI,
  			aboutMI;
  static JMenuItem[] modelMI,digitMI;
  static ResourceBundle bundle = ResourceBundle.getBundle("Messages");
  public static String[][] modelText={{"","","",""},{"","","",""},{"","","",""},
	  								{"","","",""},{"","","",""},{"","","",""},{"","","",""}};
  public static String[][] tabText={{"","","","","","",""},{"","","","","","",""}};
  public static String copyright;
  
  public static int[] geoModel={0,0,0,3,0,0,2};
  public static int CANVASSIZE=601;
  String textString;
  public void init(){
	getStrings();
    setFont(new Font("Helvetica",Font.PLAIN,12));
    javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    theMenu = new JMenuBar();
    fileMenu = new JMenu(bundle.getString("fileText"));				theMenu.add(fileMenu);
    newMI = new JMenuItem(bundle.getString("renewText"));			fileMenu.add(newMI);
    loadMI = new JMenuItem(bundle.getString("loadItText"));			fileMenu.add(loadMI);
    saveMenu = new JMenu(bundle.getString("saveItText"));			fileMenu.add(saveMenu);
    saveMI = new JMenuItem(bundle.getString("fileText"));			saveMenu.add(saveMI);
    saveJpgMI = new JMenuItem(".jpg");								saveMenu.add(saveJpgMI);
    undoMI = new JMenuItem(bundle.getString("undoText"));			fileMenu.add(undoMI);
    quitMI = new JMenuItem(bundle.getString("quitText"));			fileMenu.add(quitMI);
    makeMenu = new JMenu(bundle.getString("makeText"));				theMenu.add(makeMenu);
    pointMI = new JMenuItem(bundle.getString("pointText")); 		makeMenu.add(pointMI);
    mdPtMI = new JMenuItem(bundle.getString("mdPtText"));			makeMenu.add(mdPtMI);
    intMI = new JMenuItem(bundle.getString("intText"));				makeMenu.add(intMI);
    fxPtMI = new JMenuItem(bundle.getString("fxPtText"));			makeMenu.add(fxPtMI);
    makeMenu.addSeparator();
    lineMI = new JMenuItem(bundle.getString("lineText"));			makeMenu.add(lineMI);
    segmentMI = new JMenuItem(bundle.getString("segmentText"));		makeMenu.add(segmentMI);
    rayMI = new JMenuItem(bundle.getString("rayText"));				makeMenu.add(rayMI);
    perpMI = new JMenuItem(bundle.getString("perpText"));			makeMenu.add(perpMI);
    parllMI = new JMenuItem(bundle.getString("parllText"));			makeMenu.add(parllMI);
    bisectorMI = new JMenuItem(bundle.getString("bisectorText"));	makeMenu.add(bisectorMI);
    makeMenu.addSeparator();
    circleMI = new JMenuItem(bundle.getString("circleText"));		makeMenu.add(circleMI);
    measureMenu = new JMenu(bundle.getString("measureText"));		theMenu.add(measureMenu);
    distanceMI = new JMenuItem(bundle.getString("distanceText"));	measureMenu.add(distanceMI);
    angleMI = new JMenuItem(bundle.getString("angleText"));			measureMenu.add(angleMI);
    circumMI = new JMenuItem(bundle.getString("circumText"));		measureMenu.add(circumMI);
    areaMenu = new JMenu(bundle.getString("areaText"));				measureMenu.add(areaMenu);
    areaMI = new JMenuItem(bundle.getString("circleText"));			areaMenu.add(areaMI);
    triMI = new JMenuItem(bundle.getString("triangleText"));		areaMenu.add(triMI);
    measureMenu.addSeparator();
    sumMI = new JMenuItem(bundle.getString("sumText"));				measureMenu.add(sumMI);
    diffMI = new JMenuItem(bundle.getString("diffText"));			measureMenu.add(diffMI);
    prodMI = new JMenuItem(bundle.getString("prodText"));			measureMenu.add(prodMI);
    ratioMI = new JMenuItem(bundle.getString("ratioText"));			measureMenu.add(ratioMI);
    measureMenu.addSeparator();
    cnstMI = new JMenuItem(bundle.getString("cnstText"));			measureMenu.add(cnstMI);
    measureMenu.addSeparator();
    digitMenu = new JMenu(bundle.getString("digitText"));			measureMenu.add(digitMenu);
    moveMenu = new JMenu(bundle.getString("moveText"));				theMenu.add(moveMenu);
    moveMI = new JMenuItem(bundle.getString("transformText"));		moveMenu.add(moveMI);
    fixMI = new JMenuItem(bundle.getString("fixText"));				moveMenu.add(fixMI);
    moveMenu.addSeparator();
    trackMI = new JMenuItem(bundle.getString("trackText"));			moveMenu.add(trackMI);
    moveMenu.addSeparator();
    reflectMI = new JMenuItem(bundle.getString("reflectText"));		moveMenu.add(reflectMI);
    rotateMI = new JMenuItem(bundle.getString("rotateText"));		moveMenu.add(rotateMI);
    translateMI = new JMenuItem(bundle.getString("translateText"));	moveMenu.add(translateMI);
    moveMenu.addSeparator();
    invertMI = new JMenuItem(bundle.getString("invertText"));		moveMenu.add(invertMI);
    displayMenu = new JMenu(bundle.getString("displayText"));		theMenu.add(displayMenu);
    labelMI = new JMenuItem(bundle.getString("labelText"));			displayMenu.add(labelMI);
    commentMI = new JMenuItem(bundle.getString("commentText"));		displayMenu.add(commentMI);
    displayMenu.addSeparator();
    hideMI = new JMenuItem(bundle.getString("hideText"));			displayMenu.add(hideMI);
    showMenu = new JMenu(bundle.getString("showText"));				displayMenu.add(showMenu);
    showPtsMI = new JMenuItem(bundle.getString("pointText"));		showMenu.add(showPtsMI);
    showLnsMI = new JMenuItem(bundle.getString("lineText"));		showMenu.add(showLnsMI);
    showCrsMI = new JMenuItem(bundle.getString("circleText"));		showMenu.add(showCrsMI);
    showMeasMI = new JMenuItem(bundle.getString("measText"));		showMenu.add(showMeasMI);
    showAllMI = new JMenuItem(bundle.getString("objectsText"));		showMenu.add(showAllMI);
    displayMenu.addSeparator();
    modelMenu = new JMenu(bundle.getString("modelsText"));			displayMenu.add(modelMenu);
    displayMenu.addSeparator();
    angleMenu = new JMenu(bundle.getString("angleText"));			displayMenu.add(angleMenu);
    degreeMI = new JMenuItem("0 \u2264 \u03b8 \u2264 180");			angleMenu.add(degreeMI);
    radianMI = new JMenuItem("0 \u2264 \u03b8 \u2264 \u03c0");		angleMenu.add(radianMI);
    displayMenu.addSeparator();
    scaleMenu = new JMenu(bundle.getString("scaleText"));			displayMenu.add(scaleMenu);
    scaleUpMI = new JMenuItem(bundle.getString("upText"));			scaleMenu.add(scaleUpMI);
    scaleDnMI = new JMenuItem(bundle.getString("downText"));		scaleMenu.add(scaleDnMI); 
    modelMI = new JMenuItem[4];
    for (int i=0;i<4;i++) {
    	modelMI[i] = new JMenuItem("");					modelMenu.add(modelMI[i]);
    	modelMI[i].setAccelerator(KeyStroke.getKeyStroke((char)('w'+i)));
    	modelMI[i].addActionListener(this);
    	modelMI[i].setEnabled(false);
    	modelMI[i].setVisible(false);
    }
    digitMI = new JMenuItem[7];
    for (int i=0;i<7;i++) {
    	if (digits==i+1) digitMI[i] = new JMenuItem("\u2192");
    	else digitMI[i] = new JMenuItem("");
    	digitMenu.add(digitMI[i]);
    	digitMI[i].setAccelerator(KeyStroke.getKeyStroke((char)('2'+i)));
    	digitMI[i].addActionListener(this);
    }
    theMenu.add(Box.createHorizontalGlue());
    languageMenu = new JMenu(bundle.getString("languageText"));		theMenu.add(languageMenu);
    // language menu items placed in order translation message catalog received
    enMI = new JMenuItem(bundle.getString("english"));				languageMenu.add(enMI);
    jpMI = new JMenuItem(bundle.getString("japanese"));				languageMenu.add(jpMI);
    esMI = new JMenuItem(bundle.getString("spanish"));				languageMenu.add(esMI);
    mrMI = new JMenuItem(bundle.getString("marathi"));				languageMenu.add(mrMI);
    frMI = new JMenuItem(bundle.getString("french"));				languageMenu.add(frMI);
    arMI = new JMenuItem(bundle.getString("arabic"));				languageMenu.add(arMI);
    koMI = new JMenuItem(bundle.getString("korean"));				languageMenu.add(koMI);
    ruMI = new JMenuItem(bundle.getString("russian"));				languageMenu.add(ruMI);
    huMI = new JMenuItem(bundle.getString("magyar"));				languageMenu.add(huMI);
    zhMI = new JMenuItem(bundle.getString("chinese"));				languageMenu.add(zhMI);
    itMI = new JMenuItem(bundle.getString("italian"));				languageMenu.add(itMI);
    ptMI = new JMenuItem(bundle.getString("portuguese"));			languageMenu.add(ptMI);
    trMI = new JMenuItem(bundle.getString("turkish"));				languageMenu.add(trMI);
    hiMI = new JMenuItem(bundle.getString("hindi"));				languageMenu.add(hiMI);
    heMI = new JMenuItem(bundle.getString("hebrew"));				languageMenu.add(heMI);
    helpMenu = new JMenu(bundle.getString("helpText"));				theMenu.add(helpMenu);
    aboutMI = new JMenuItem(bundle.getString("aboutText"));			helpMenu.add(aboutMI);
    
    newMI.setAccelerator(KeyStroke.getKeyStroke('N',ActionEvent.CTRL_MASK));
    saveMI.setAccelerator(KeyStroke.getKeyStroke('S',ActionEvent.CTRL_MASK));
    saveJpgMI.setAccelerator(KeyStroke.getKeyStroke('J',ActionEvent.CTRL_MASK));
    loadMI.setAccelerator(KeyStroke.getKeyStroke('O',ActionEvent.CTRL_MASK));
    undoMI.setAccelerator(KeyStroke.getKeyStroke('Z',ActionEvent.CTRL_MASK));
    quitMI.setAccelerator(KeyStroke.getKeyStroke('Q',ActionEvent.CTRL_MASK));
    pointMI.setAccelerator(KeyStroke.getKeyStroke('.'));
    mdPtMI.setAccelerator(KeyStroke.getKeyStroke('m'));
    fxPtMI.setAccelerator(KeyStroke.getKeyStroke(','));
    intMI.setAccelerator(KeyStroke.getKeyStroke('i'));
    lineMI.setAccelerator(KeyStroke.getKeyStroke('l'));
    segmentMI.setAccelerator(KeyStroke.getKeyStroke('s'));
    perpMI.setAccelerator(KeyStroke.getKeyStroke('p'));
    parllMI.setAccelerator(KeyStroke.getKeyStroke('q'));
    rayMI.setAccelerator(KeyStroke.getKeyStroke('r'));
    bisectorMI.setAccelerator(KeyStroke.getKeyStroke('b'));
    circleMI.setAccelerator(KeyStroke.getKeyStroke('c'));
    distanceMI.setAccelerator(KeyStroke.getKeyStroke('d'));
    angleMI.setAccelerator(KeyStroke.getKeyStroke('a'));
    circumMI.setAccelerator(KeyStroke.getKeyStroke('C',ActionEvent.SHIFT_MASK));
    areaMI.setAccelerator(KeyStroke.getKeyStroke('O',ActionEvent.SHIFT_MASK));
    triMI.setAccelerator(KeyStroke.getKeyStroke('T',ActionEvent.SHIFT_MASK));
    sumMI.setAccelerator(KeyStroke.getKeyStroke('+'));
    diffMI.setAccelerator(KeyStroke.getKeyStroke('-'));
    prodMI.setAccelerator(KeyStroke.getKeyStroke('*'));
    ratioMI.setAccelerator(KeyStroke.getKeyStroke('/'));
    cnstMI.setAccelerator(KeyStroke.getKeyStroke('k'));
    moveMI.setAccelerator(KeyStroke.getKeyStroke('t'));
    fixMI.setAccelerator(KeyStroke.getKeyStroke('f'));
    trackMI.setAccelerator(KeyStroke.getKeyStroke('?'));
    reflectMI.setAccelerator(KeyStroke.getKeyStroke('!'));
    rotateMI.setAccelerator(KeyStroke.getKeyStroke('@'));
    translateMI.setAccelerator(KeyStroke.getKeyStroke('#'));
    invertMI.setAccelerator(KeyStroke.getKeyStroke('$'));
    hideMI.setAccelerator(KeyStroke.getKeyStroke('h'));
    showAllMI.setAccelerator(KeyStroke.getKeyStroke('U',ActionEvent.SHIFT_MASK));
    labelMI.setAccelerator(KeyStroke.getKeyStroke('L',ActionEvent.SHIFT_MASK));
    commentMI.setAccelerator(KeyStroke.getKeyStroke('"'));
    degreeMI.setAccelerator(KeyStroke.getKeyStroke('o'));
    radianMI.setAccelerator(KeyStroke.getKeyStroke('R',ActionEvent.SHIFT_MASK));
    scaleUpMI.setAccelerator(KeyStroke.getKeyStroke('>')); 
    scaleDnMI.setAccelerator(KeyStroke.getKeyStroke('<'));
    
    newMI.addActionListener(this);
    saveMI.addActionListener(this); //saveMI.setEnabled(false);
    saveJpgMI.addActionListener(this);
    loadMI.addActionListener(this);
    undoMI.addActionListener(this); //undoMI.setEnabled(false);
    quitMI.addActionListener(this);
    pointMI.addActionListener(this);
    mdPtMI.addActionListener(this);
    fxPtMI.addActionListener(this);
    intMI.addActionListener(this);
    lineMI.addActionListener(this);
    segmentMI.addActionListener(this);
    perpMI.addActionListener(this);
    parllMI.addActionListener(this);
    rayMI.addActionListener(this);
    bisectorMI.addActionListener(this);
    circleMI.addActionListener(this);
    distanceMI.addActionListener(this);
    angleMI.addActionListener(this);
    circumMI.addActionListener(this);
    areaMI.addActionListener(this);
    triMI.addActionListener(this);
    ratioMI.addActionListener(this);
    sumMI.addActionListener(this);
    diffMI.addActionListener(this);
    prodMI.addActionListener(this);
    cnstMI.addActionListener(this);
    moveMI.addActionListener(this);
    fixMI.addActionListener(this);
    trackMI.addActionListener(this);
    reflectMI.addActionListener(this);
    rotateMI.addActionListener(this);
    translateMI.addActionListener(this);
    invertMI.addActionListener(this);
    hideMI.addActionListener(this);
    showPtsMI.addActionListener(this);
    showLnsMI.addActionListener(this);
    showCrsMI.addActionListener(this);
    showMeasMI.addActionListener(this);
    showAllMI.addActionListener(this);
    labelMI.addActionListener(this);
    commentMI.addActionListener(this);
    degreeMI.addActionListener(this);
    radianMI.addActionListener(this);
    scaleUpMI.addActionListener(this);
    scaleDnMI.addActionListener(this);
    enMI.addActionListener(this);
    esMI.addActionListener(this);
    frMI.addActionListener(this);
    jpMI.addActionListener(this);
    mrMI.addActionListener(this);
    arMI.addActionListener(this);
    koMI.addActionListener(this);
    ruMI.addActionListener(this);
    huMI.addActionListener(this);
    zhMI.addActionListener(this);
    itMI.addActionListener(this);
    ptMI.addActionListener(this);
    trMI.addActionListener(this);
    hiMI.addActionListener(this);
    heMI.addActionListener(this);
    aboutMI.addActionListener(this);
    
    setLayout(new BorderLayout());
    cntrPanel = new JPanel();
    tabbedPanePlane = new JTabbedPane();
    canvasPanel = new JPanel[7];
 
    cntrPanel.setLayout(new BorderLayout());
    
    canvas=new GeoCanvas[7];
    infoLabel = new JLabel[7];
    infoPanel = new JPanel[7];
    modelLabel = new JLabel[7];
    modelPanel = new JPanel[7];
    for(int i=0;i<7;i++){
      canvasPanel[i]=new JPanel();
      canvasPanel[i].setLayout(new BorderLayout());
      infoPanel[i]=new JPanel();
      modelPanel[i]=new JPanel();
      switch(i){
      case 0:
          canvas[i] = new GeoCanvas(new SphericalGeometry());
          textString= bundle.getString("SphBigText");
          break;
      case 1:
    	  canvas[i] = new GeoCanvas(new ProjectiveGeometry());
    	  textString= bundle.getString("PrjBigText");
    	  break;      
      case 2:
          canvas[i] = new GeoCanvas(new EuclideanGeometry());
          textString= bundle.getString("EucBigText");
          break;
      case 3:
    	  canvas[i] = new GeoCanvas(new HyperbolicGeometry());
		  textString= bundle.getString("HypBigText");
		  break;
	  case 4: 
		  canvas[i] = new GeoCanvas(new ManhattanGeometry());
    	  textString= bundle.getString("ManBigText");
    	  break;
	  case 5:
    	  canvas[i] = new GeoCanvas(new ToroidalGeometry());
    	  textString= bundle.getString("TorBigText");
    	  break;
	  case 6:
    	  canvas[i] = new GeoCanvas(new ConicalGeometry());
    	  textString= bundle.getString("ConBigText");
    	  break;
      }
      tabbedPanePlane.addTab(textString, canvasPanel[i]);
      canvas[i].setBackground(Color.white);
      canvas[i].setSize(CANVASSIZE,CANVASSIZE);
      canvasPanel[i].add("Center",canvas[i]);
      textString=bundle.getString("DefaultText");
      infoLabel[i]=new JLabel(textString);
      infoPanel[i].add(infoLabel[i]);
      textString="<html><font color='blue'>"+modelText[i][geoModel[i]]+"</font></html>";
      modelLabel[i]=new JLabel(textString);
      modelPanel[i].add(modelLabel[i]);
      canvasPanel[i].add("South",infoPanel[i]);
      canvasPanel[i].add("North",modelPanel[i]);
      canvas[i].addChangeListener(this);
    }
    tabbedPanePlane.addChangeListener(this);
    cntrPanel.add("Center",tabbedPanePlane);
    tabbedPanePlane.setSelectedIndex(2);
	add("Center",cntrPanel);
    setInfoTA();
  }
  private void getStrings() {
	  bundle = ResourceBundle.getBundle("Messages");
	  copyright=bundle.getString("copyright");
	  frame.setTitle(copyright);
      tabText[0][0]=bundle.getString("SphBigText");
      tabText[0][1]=bundle.getString("PrjBigText");
      tabText[0][2]=bundle.getString("EucBigText");
      tabText[0][3]=bundle.getString("HypBigText");
      tabText[0][4]=bundle.getString("ManBigText");
      tabText[0][5]=bundle.getString("TorBigText");
      tabText[0][6]=bundle.getString("ConBigText");
      tabText[1][0]=bundle.getString("SphSmallText");
      tabText[1][1]=bundle.getString("PrjSmallText");
      tabText[1][2]=bundle.getString("EucSmallText");
      tabText[1][3]=bundle.getString("HypSmallText");
      tabText[1][4]=bundle.getString("ManSmallText");
      tabText[1][5]=bundle.getString("TorSmallText");
      tabText[1][6]=bundle.getString("ConSmallText");
      modelText[0][0]=bundle.getString("sphere");
      modelText[0][1]=bundle.getString("plane");
      modelText[0][2]=bundle.getString("mercator");
      modelText[1][0]=bundle.getString("halfSphere");
      modelText[1][1]=bundle.getString("plane");
      modelText[2][0]=bundle.getString("plane");
      modelText[2][1]=bundle.getString("projective");
      modelText[2][2]=bundle.getString("inverted");
      modelText[3][0]=bundle.getString("poincare");
      modelText[3][1]=bundle.getString("klein");
      modelText[3][2]=bundle.getString("halfPlane");
      modelText[3][3]=bundle.getString("weierstrass");
      modelText[4][0]=bundle.getString("plane");
      modelText[5][0]=bundle.getString("square");
      modelText[5][1]=bundle.getString("tiling");
      modelText[5][2]=bundle.getString("donut");
      modelText[6][0]=bundle.getString("plane");
      modelText[6][1]=bundle.getString("tiling");
      modelText[6][2]=bundle.getString("cone");
      commentText=bundle.getString("commentText");
      cnstText=bundle.getString("cnstText");
      newLabelText=bundle.getString("newLabelText");
      currentLabelText=bundle.getString("currentLabelText");
      
  }
  private void setInfoTA() {
	//if (canvas[tabbedPanePlane.getSelectedIndex()].list.size()>0) {saveMI.setEnabled(true);undoMI.setEnabled(true);}
	//else {/*saveMI.setEnabled(false);undoMI.setEnabled(false);*/}
    switch(whatToDo) {
    case MakePoints:	textString=bundle.getString("MakePointsText");	break;
    case MakeMdPt:		textString=bundle.getString("MakeMdPtText");	break;
    case MakeFxPt:		textString=bundle.getString("MakeFxPtText");	break;
    case MakeInt:		textString=bundle.getString("MakeIntText");		break;
    case MakeSegment:	textString=bundle.getString("MakeSegmentText");	break;
    case MakeLines:		textString=bundle.getString("MakeLinesText");	break;
    case MakePerps:		textString=bundle.getString("MakePerpsText");	break;
    case MakeParlls:	textString=bundle.getString("MakeParllsText");	break;
    case MakeRays:		textString=bundle.getString("MakeRaysText");	break;
    case MakeBisect:	textString=bundle.getString("MakeBisectText");	break;
    case MakeCircles:	textString=bundle.getString("MakeCirclesText");	break;
    case MeasureDist:	textString=bundle.getString("MeasureDistText");	break;
    case MeasureAngle:	textString=bundle.getString("MeasureAngleText");break;
  	case MeasureCirc:	textString=bundle.getString("MeasureCircText");	break;
    case MeasureArea:	textString=bundle.getString("MeasureAreaText");	break;
    case MeasureTri:	textString=bundle.getString("MeasureTriText");	break;
    case MeasureRatio:	textString=bundle.getString("MeasureRatioText");break;
    case MeasureSum:	textString=bundle.getString("MeasureSumText");	break;
    case MeasureDiff:	textString=bundle.getString("MeasureDiffText");	break;
    case MeasureProd:	textString=bundle.getString("MeasureProdText");	break;
    case MeasureCnst:	textString=bundle.getString("MeasureCnstText");	break;
    case Move:			textString=bundle.getString("MoveText");		break;
    case FixObject:		textString=bundle.getString("FixObjectText");	break;
    case TrackObject:	textString=bundle.getString("TrackPtText");		break;
    case ReflectPt:		textString=bundle.getString("ReflectPtText");	break;
    case RotatePt:		textString=bundle.getString("RotatePtText");	break;
    case TranslatePt:	textString=bundle.getString("TranslatePtText");	break;
    case InvertPt:		textString=bundle.getString("InvertPtText");	break;
    case HideObject:	textString=bundle.getString("HideObjectText");	break;
    case LabelObject:	textString=bundle.getString("LabelObjectText");	break;
    case MakeComment:	textString=bundle.getString("MakeCmntText");	break;
	default:			textString=bundle.getString("DefaultText");		break;
    }
    infoLabel[tabbedPanePlane.getSelectedIndex()].setText(textString);
	if (tabbedPanePlane.getSelectedIndex()>4) {
		intMI.setEnabled(false);
		reflectMI.setEnabled(false);
		rotateMI.setEnabled(false);
		invertMI.setEnabled(false);
		if (tabbedPanePlane.getSelectedIndex()==6){
			translateMI.setEnabled(false);
			fixMI.setEnabled(false);
			parllMI.setEnabled(false);
		}
		else {
			translateMI.setEnabled(true);
			fixMI.setEnabled(true);
			parllMI.setEnabled(true);
		}
	}
	else {
		intMI.setEnabled(true);
		reflectMI.setEnabled(true);
		rotateMI.setEnabled(true);
		invertMI.setEnabled(true);
	}
	//parameter[0]="http://www.plu.edu/~heathdj/java/geom/Playground/test.euc";
	if (parameter[0].contains("http://")) {
		if (parameter[0].contains(".sph")) 		tabbedPanePlane.setSelectedIndex(0);
		else if (parameter[0].contains(".prj")) tabbedPanePlane.setSelectedIndex(1);
		else if (parameter[0].contains(".euc")) tabbedPanePlane.setSelectedIndex(2);
		else if (parameter[0].contains(".hyp")) tabbedPanePlane.setSelectedIndex(3);
		else if (parameter[0].contains(".man")) tabbedPanePlane.setSelectedIndex(4);
		else if (parameter[0].contains(".tor")) tabbedPanePlane.setSelectedIndex(5);
		else if (parameter[0].contains(".con")) tabbedPanePlane.setSelectedIndex(6);
	}
	if (parameter[0].contains("http://")) {											// if parameter[0] contains a URL
	  lastOpenDir=parameter[0];														// load the file at that URL
	  SaveLoad.load(canvas[tabbedPanePlane.getSelectedIndex()].list,canvas[tabbedPanePlane.getSelectedIndex()]);
	  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++) {	//
		  double[] v={0,0,0};														//
		  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getXYZ(v);			//
		  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setNewXYZ(v);		//
	  }																				//
	  parameter[0]="~";																//
	  if (!parameter[1].equals(""))											// if parameter[1] isn't empty
		  if (Integer.parseInt(parameter[1])>=0 &&							// and is an integer from 0 to 3
				  Integer.parseInt(parameter[1])<4)							// then look at what geometry we
			  switch(tabbedPanePlane.getSelectedIndex()) {					// are in.  If:
			  case 4:														// Manhattan,
				  parameter[1]="0";											//	then model=0
			  case 1:														// Projective
				  if (Integer.parseInt(parameter[1])>1) parameter[1]="0";	//	then model=0 or 1
			  case 6:														// Conical
			  case 5:														// Toroidal
			  case 2:														// Euclidean
			  case 0:														// Spherical
				  if (Integer.parseInt(parameter[1])>2) parameter[1]="0";	//	then model=0, 1, or 2
			  default:														// Hyperbolic
				  model=Integer.parseInt(parameter[1]);						//  set model number
			  }																//
	}
	setupModel();
	canvas[tabbedPanePlane.getSelectedIndex()].repaint();
  }
  public void setupModel() {
	  geoModel[tabbedPanePlane.getSelectedIndex()]=model;
	  modelLabel[tabbedPanePlane.getSelectedIndex()].setText("<html><font color='blue'>"+
			modelText[tabbedPanePlane.getSelectedIndex()][model]+"</font></html>");
	  if ((whatToDo==MakeInt || whatToDo==ReflectPt || whatToDo==RotatePt || whatToDo==InvertPt)
			  && tabbedPanePlane.getSelectedIndex()>4) whatToDo=MakePoints;
	  if ((whatToDo==MakeParlls) && tabbedPanePlane.getSelectedIndex()<2) whatToDo=MakePoints;
	  if (tabbedPanePlane.getSelectedIndex()<2 || tabbedPanePlane.getSelectedIndex()==6)	parllMI.setEnabled(false);
	  else parllMI.setEnabled(true);
	  if ((tabbedPanePlane.getSelectedIndex()==2 && model==0) ||
		  (tabbedPanePlane.getSelectedIndex()==3 && model==3) ||
		  (tabbedPanePlane.getSelectedIndex()==1 && model==1) ||
		  (tabbedPanePlane.getSelectedIndex()==0 && model==1) ||
		  (tabbedPanePlane.getSelectedIndex()==6)) {
		    scaleMenu.setEnabled(true);
		    scaleUpMI.setEnabled(true);
		    scaleDnMI.setEnabled(true);
	  }
	  else {
	        scaleMenu.setEnabled(false);
		    scaleUpMI.setEnabled(false);
		    scaleDnMI.setEnabled(false);
	  }
	  //if (tabbedPanePlane.getSelectedIndex()==4) modelMenu.setEnabled(false);
	  //else modelMenu.setEnabled(true);
  }
  public void stateChanged(ChangeEvent e) {
    if (e.getSource()==tabbedPanePlane) model=geoModel[tabbedPanePlane.getSelectedIndex()];
    setupModel();
    setInfoTA();
  }
  public void actionPerformed(ActionEvent e){
	  canvas[tabbedPanePlane.getSelectedIndex()].clickedList.clear();
	  if (e.getSource()==pointMI)	whatToDo=MakePoints;
	  if (e.getSource()==mdPtMI)	whatToDo=MakeMdPt;
	  if (e.getSource()==fxPtMI)	whatToDo=MakeFxPt;
	  if (e.getSource()==intMI)		whatToDo=MakeInt;
	  if (e.getSource()==lineMI)	whatToDo=MakeLines;
	  if (e.getSource()==segmentMI)	whatToDo=MakeSegment;
	  if (e.getSource()==perpMI)	whatToDo=MakePerps;
	  if (e.getSource()==parllMI)	whatToDo=MakeParlls;
	  if (e.getSource()==rayMI)		whatToDo=MakeRays;
	  if (e.getSource()==bisectorMI)whatToDo=MakeBisect;
	  if (e.getSource()==circleMI)	whatToDo=MakeCircles;
	  if (e.getSource()==distanceMI)whatToDo=MeasureDist;
	  if (e.getSource()==degreeMI)	degrees=true;
	  if (e.getSource()==radianMI)	degrees=false;
	  if (e.getSource()==angleMI)	whatToDo=MeasureAngle;
	  if (e.getSource()==circumMI)	whatToDo=MeasureCirc;
	  if (e.getSource()==areaMI)	whatToDo=MeasureArea;
	  if (e.getSource()==triMI)		whatToDo=MeasureTri;
	  if (e.getSource()==ratioMI)	whatToDo=MeasureRatio;
	  if (e.getSource()==sumMI)		whatToDo=MeasureSum;
	  if (e.getSource()==diffMI)	whatToDo=MeasureDiff;
	  if (e.getSource()==prodMI)	whatToDo=MeasureProd;
	  if (e.getSource()==cnstMI)	whatToDo=MeasureCnst;
	  if (e.getSource()==moveMI)	whatToDo=Move;
	  if (e.getSource()==fixMI)		whatToDo=FixObject;
	  if (e.getSource()==trackMI)	whatToDo=TrackObject;
	  if (e.getSource()==reflectMI)	whatToDo=ReflectPt;
	  if (e.getSource()==rotateMI)	whatToDo=RotatePt;
	  if (e.getSource()==translateMI)whatToDo=TranslatePt;
	  if (e.getSource()==invertMI)	whatToDo=InvertPt;
	  if (e.getSource()==hideMI)	whatToDo=HideObject;
	  if (e.getSource()==labelMI)	whatToDo=LabelObject;
	  if (e.getSource()==commentMI)	whatToDo=MakeComment;
	  if (e.getSource()==scaleUpMI)	{
		  canvas[tabbedPanePlane.getSelectedIndex()].geometry.setScale(1);
		  double[] v={0,0,0};
		  if (tabbedPanePlane.getSelectedIndex()==6)
			  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++) {
				  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).update();
				  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getNewXYZ(v);
				  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setXYZ(v);
			  }
		  
	  }
	  if (e.getSource()==scaleDnMI) {
		  canvas[tabbedPanePlane.getSelectedIndex()].geometry.setScale(-1);
		  double[] v={0,0,0};
		  if (tabbedPanePlane.getSelectedIndex()==6)
			  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++) {
				  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).update();
				  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getNewXYZ(v);
				  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setXYZ(v);
			  }
	  }
	  for (int i=0;i<4;i++) if (e.getSource()==modelMI[i]) {
		getStrings();
		model=i;
		setupModel();
	  }
	  if (e.getSource()==newMI)	{
		  canvas[tabbedPanePlane.getSelectedIndex()].trackPoint=null;
		  canvas[tabbedPanePlane.getSelectedIndex()].fixedObject=null;
		  canvas[tabbedPanePlane.getSelectedIndex()].list.clear();
		  canvas[tabbedPanePlane.getSelectedIndex()].potentialClick=null;
		  canvas[tabbedPanePlane.getSelectedIndex()].clickedList.clear();
		  whatToDo=-1;
	  }
	  if (e.getSource()==showPtsMI)	{
		  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++)
	          if (canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getType()>0 &&
	        	  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getType()<30 &&
	        	  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getType()!=GeoConstruct.FIXedPT)
			  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setShown(true);
	  }
	  if (e.getSource()==showLnsMI)	{
		  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++)
	          if (canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getType()<0)
			  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setShown(true);
	  }
	  if (e.getSource()==showMeasMI)	{
		  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++)
	          if (canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getType()>30)
			  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setShown(true);
	  }
	  if (e.getSource()==showCrsMI)	{
		  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++)
	          if (canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getType()==0)
			  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setShown(true);
	  }
	  if (e.getSource()==showAllMI) {
		  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++)
	          canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setShown(true);
	  }
	    
	  if (e.getSource()==saveMI){
      SaveLoad.save(canvas[tabbedPanePlane.getSelectedIndex()].list,canvas[tabbedPanePlane.getSelectedIndex()]);
      }
	  
	  if (e.getSource()==saveJpgMI){
	      SaveLoad.saveImage(canvas[tabbedPanePlane.getSelectedIndex()]);
	      }
    if (e.getSource()==loadMI){
	  canvas[tabbedPanePlane.getSelectedIndex()].fixedObject=null;
	  SaveLoad.load(canvas[tabbedPanePlane.getSelectedIndex()].list,canvas[tabbedPanePlane.getSelectedIndex()]);
	  for (int i=0;i<canvas[tabbedPanePlane.getSelectedIndex()].list.size();i++) {
		  double[] v={0,0,0};
		  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).getXYZ(v);
		  canvas[tabbedPanePlane.getSelectedIndex()].list.get(i).setNewXYZ(v);
	  }
	  
    }
    if (e.getSource()==undoMI){
    	if (canvas[tabbedPanePlane.getSelectedIndex()].list.size()>0) {
    		int n = canvas[tabbedPanePlane.getSelectedIndex()].list.getLast().getType();
    		if (n==11) // if type=CircIntCirc11 then we undo 4 points at once
    			for (int i=0;i<4;i++) {
    				if (canvas[tabbedPanePlane.getSelectedIndex()].trackPoint==canvas[tabbedPanePlane.getSelectedIndex()].list.getLast())
    	    			canvas[tabbedPanePlane.getSelectedIndex()].trackPoint=null; 	// if undoing a trackPoint, nullify the trackPoint.
    	    		if (canvas[tabbedPanePlane.getSelectedIndex()].fixedObject==canvas[tabbedPanePlane.getSelectedIndex()].list.getLast())
    	    			canvas[tabbedPanePlane.getSelectedIndex()].fixedObject=null; // if undoing a fixed object, nullify the fixedObject.
    	    		canvas[tabbedPanePlane.getSelectedIndex()].list.removeLast();
    			}
    		else if (n==9 || n==7 || n==5 || n==-5) // if type=CircIntCirc01, CircIntLine1, LineIntLine1, or Parll1
    			for (int i=0;i<2;i++) {				// we undo 2 objects at once.
    				if (canvas[tabbedPanePlane.getSelectedIndex()].trackPoint==canvas[tabbedPanePlane.getSelectedIndex()].list.getLast())
    	    			canvas[tabbedPanePlane.getSelectedIndex()].trackPoint=null; 	// if undoing a trackPoint, nullify the trackPoint.
    	    		if (canvas[tabbedPanePlane.getSelectedIndex()].fixedObject==canvas[tabbedPanePlane.getSelectedIndex()].list.getLast())
    	    			canvas[tabbedPanePlane.getSelectedIndex()].fixedObject=null; // if undoing a fixed object, nullify the fixedObject.
    	    		canvas[tabbedPanePlane.getSelectedIndex()].list.removeLast();
    			}
    		else {
    			if (canvas[tabbedPanePlane.getSelectedIndex()].trackPoint==canvas[tabbedPanePlane.getSelectedIndex()].list.getLast())
	    			canvas[tabbedPanePlane.getSelectedIndex()].trackPoint=null; 	// if undoing a trackPoint, nullify the trackPoint.
	    		if (canvas[tabbedPanePlane.getSelectedIndex()].fixedObject==canvas[tabbedPanePlane.getSelectedIndex()].list.getLast())
	    			canvas[tabbedPanePlane.getSelectedIndex()].fixedObject=null;
    			canvas[tabbedPanePlane.getSelectedIndex()].list.removeLast();	// otherwise we undo one at a time
    		}
    		canvas[tabbedPanePlane.getSelectedIndex()].clickedList.clear();
    		canvas[tabbedPanePlane.getSelectedIndex()].potentialClick=null;
    	}
    	//if (canvas[tabbedPanePlane.getSelectedIndex()].list.size()==0) undoMI.setEnabled(false);
    }
    if (e.getSource()==quitMI){
    	System.exit(0);
    }
    if (e.getSource()==enMI) Locale.setDefault(Locale.ENGLISH);
    if (e.getSource()==esMI) Locale.setDefault(new Locale("es","",""));// Spanish
    if (e.getSource()==frMI) Locale.setDefault(Locale.FRENCH);
    if (e.getSource()==jpMI) Locale.setDefault(Locale.JAPANESE);
    if (e.getSource()==mrMI) Locale.setDefault(new Locale("mr","",""));//marathi
    if (e.getSource()==arMI) Locale.setDefault(new Locale("ar","",""));//arabic
    if (e.getSource()==koMI) Locale.setDefault(new Locale("ko","",""));//korean
    if (e.getSource()==ruMI) Locale.setDefault(new Locale("ru","",""));//russian
    if (e.getSource()==huMI) Locale.setDefault(new Locale("hu","",""));//hungarian
    if (e.getSource()==zhMI) Locale.setDefault(Locale.CHINESE);
    if (e.getSource()==itMI) Locale.setDefault(Locale.ITALIAN);
    if (e.getSource()==ptMI) Locale.setDefault(new Locale("pt","",""));//portuguese
    if (e.getSource()==trMI) Locale.setDefault(new Locale("tr","",""));//turkish
    if (e.getSource()==hiMI) Locale.setDefault(new Locale("hi","",""));//hindi
    if (e.getSource()==heMI) Locale.setDefault(new Locale("he","",""));//hebrew
    
    if (e.getSource()==enMI || e.getSource()==esMI ||
    	e.getSource()==frMI || e.getSource()==jpMI ||
    	e.getSource()==mrMI || e.getSource()==arMI ||
    	e.getSource()==koMI || e.getSource()==ruMI ||
    	e.getSource()==huMI || e.getSource()==zhMI ||
    	e.getSource()==itMI || e.getSource()==ptMI ||
    	e.getSource()==trMI || e.getSource()==hiMI ||
    	e.getSource()==heMI)
    {
    	getStrings();
    	languageMenu.setText(bundle.getString("languageText"));
    	fileMenu.setText(bundle.getString("fileText"));
        newMI.setText(bundle.getString("renewText"));
        loadMI.setText(bundle.getString("loadItText"));
        saveMenu.setText(bundle.getString("saveItText"));
        saveMI.setText(bundle.getString("fileText"));
        undoMI.setText(bundle.getString("undoText"));
        quitMI.setText(bundle.getString("quitText"));
        makeMenu.setText(bundle.getString("makeText"));
        pointMI.setText(bundle.getString("pointText"));
        mdPtMI.setText(bundle.getString("mdPtText"));
        fxPtMI.setText(bundle.getString("fxPtText"));
        intMI.setText(bundle.getString("intText"));
        lineMI.setText(bundle.getString("lineText"));
        segmentMI.setText(bundle.getString("segmentText"));
        perpMI.setText(bundle.getString("perpText"));
        parllMI.setText(bundle.getString("parllText"));
        rayMI.setText(bundle.getString("rayText"));
        bisectorMI.setText(bundle.getString("bisectorText"));
        circleMI.setText(bundle.getString("circleText"));
        measureMenu.setText(bundle.getString("measureText"));
        distanceMI.setText(bundle.getString("distanceText"));
        angleMI.setText(bundle.getString("angleText"));
        circumMI.setText(bundle.getString("circumText"));
        areaMenu.setText(bundle.getString("areaText"));
        areaMI.setText(bundle.getString("circleText"));
        triMI.setText(bundle.getString("triangleText"));
        ratioMI.setText(bundle.getString("ratioText"));
        sumMI.setText(bundle.getString("sumText"));
        diffMI.setText(bundle.getString("diffText"));
        prodMI.setText(bundle.getString("prodText"));
        digitMenu.setText(bundle.getString("digitText"));
        cnstMI.setText(bundle.getString("cnstText"));
        moveMenu.setText(bundle.getString("moveText"));
        moveMI.setText(bundle.getString("transformText"));
        fixMI.setText(bundle.getString("fixText"));
        trackMI.setText(bundle.getString("trackText"));
        reflectMI.setText(bundle.getString("reflectText"));
        rotateMI.setText(bundle.getString("rotateText"));
        translateMI.setText(bundle.getString("translateText"));
        invertMI.setText(bundle.getString("invertText"));
        displayMenu.setText(bundle.getString("displayText"));
        labelMI.setText(bundle.getString("labelText"));
        commentMI.setText(bundle.getString("commentText"));
        hideMI.setText(bundle.getString("hideText"));
        showMenu.setText(bundle.getString("showText"));
        showPtsMI.setText(bundle.getString("pointText"));
        showLnsMI.setText(bundle.getString("lineText"));
        showCrsMI.setText(bundle.getString("circleText"));
        showMeasMI.setText(bundle.getString("measText"));
        showAllMI.setText(bundle.getString("objectsText"));
        modelMenu.setText(bundle.getString("modelsText"));
        angleMenu.setText(bundle.getString("angleText"));
        scaleMenu.setText(bundle.getString("scaleText"));
        scaleUpMI.setText(bundle.getString("upText"));
        scaleDnMI.setText(bundle.getString("downText"));
        helpMenu.setText(bundle.getString("helpText"));
        aboutMI.setText(bundle.getString("aboutText"));
        for (int j=0;j<7;j++)
			  if (tabbedPanePlane.getSelectedIndex()==j) {
			    modelLabel[j].setText("<html><font color='blue'>"+
			    		              modelText[j][geoModel[j]]+"</font></html>");
			  }
    }
    if (e.getSource()==aboutMI) {
    	BareBonesBrowserLaunch.openURL("https://github.com/joshicola/geometry_playground_java/wiki/Instructions");
    }
    for (int i=0;i<7;i++) if (e.getSource()==digitMI[i]) {
    	digitMI[digits-1].setText("");
    	digits=i+1;
    	digitMI[digits-1].setText("\u2192");
    }
	  
    setInfoTA();
  }
  /**
   * Create the GUI and show it.  For thread safety, 
   * this method should be invoked from the 
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {  
	    
      //Create and set up the window.
	  bundle = ResourceBundle.getBundle("Messages");
	  copyright=bundle.getString("copyright");
	  frame = new JFrame(copyright);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// \u03b2=beta

      //Create and set up the content pane.
      GeoPlayground newContentPane = new GeoPlayground();
      newContentPane.setOpaque(true); //content panes must be opaque
      newContentPane.init();
      frame.setContentPane(newContentPane);
      //Display the window.
      frame.setJMenuBar(theMenu);
      frame.pack();
      frame.setVisible(true);
  }
  public static void main(String[] args) {
	if (args.length>0) GeoPlayground.parameter[0]=args[0];
	if (args.length>1) GeoPlayground.parameter[1]=args[1];
      //Schedule a job for the event-dispatching thread:
      //creating and showing this application's GUI.
	  
	  //Locale.setDefault(Locale.JAPAN); //This is to test the languages capability
	  javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              createAndShowGUI(); 
          }
      });
  }
  public static int getHght() {
    return (int)MathEqns.max(canvas[tabbedPanePlane.getSelectedIndex()].getHeight(),4);
  }
  public static int getWdth() {
    return (int)MathEqns.max(canvas[tabbedPanePlane.getSelectedIndex()].getWidth(),4);
  }
}