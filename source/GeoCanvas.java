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
 
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.lang.Double;

import javax.swing.event.ChangeListener;
import javax.swing.JOptionPane;

class GeoCanvas extends Canvas implements MouseListener, MouseMotionListener {
	protected static int SZ= GeoPlayground.CANVASSIZE/2;
	private final int BISECTOR=GeoConstruct.BISECTOR,
	RAY=GeoConstruct.RAY,
	SEGMENT=GeoConstruct.SEGMENT,
	PARLL0=GeoConstruct.PARLL0,
	PARLL1=GeoConstruct.PARLL1,
	PERP=GeoConstruct.PERP,
	LINE=GeoConstruct.LINE,
	CIRCLE=GeoConstruct.CIRCLE,
	POINT=GeoConstruct.POINT,
	PTonLINE=GeoConstruct.PTonLINE,
	PTonCIRC=GeoConstruct.PTonCIRC,
	LINEintLINE0=GeoConstruct.LINEintLINE0,
	LINEintLINE1=GeoConstruct.LINEintLINE1,
	CIRCintLINE0=GeoConstruct.CIRCintLINE0,
	CIRCintLINE1=GeoConstruct.CIRCintLINE1,
	CIRCintCIRC00=GeoConstruct.CIRCintCIRC00,
	CIRCintCIRC01=GeoConstruct.CIRCintCIRC01,
	CIRCintCIRC10=GeoConstruct.CIRCintCIRC10,
	CIRCintCIRC11=GeoConstruct.CIRCintCIRC11,
	MIDPT=GeoConstruct.MIDPT,
	FIXedPT=GeoConstruct.FIXedPT,
	REFLECT_PT=GeoConstruct.REFLECT_PT,
	ROTATE_PT=GeoConstruct.ROTATE_PT,
	TRANSLATE_PT=GeoConstruct.TRANSLATE_PT,
	INVERT_PT=GeoConstruct.INVERT_PT,
	DISTANCE=GeoConstruct.DISTANCE,
	AREA=GeoConstruct.AREA,
	CIRCUMF=GeoConstruct.CIRCUMF,
	ANGLE=GeoConstruct.ANGLE,
	RATIO=GeoConstruct.RATIO,
	SUM=GeoConstruct.SUM,
	DIFF=GeoConstruct.DIFF,
	PROD=GeoConstruct.PROD,
	TRIANGLE=GeoConstruct.TRIANGLE,
	CONSTANT=GeoConstruct.CONSTANT,
	COMMENT=GeoConstruct.COMMENT;
	private boolean alreadyMoving=false;
	protected ChangeListener geometryListener;
	protected GeoMetry geometry=null;
	static ResourceBundle bundle = ResourceBundle.getBundle("Messages");
	public LinkedList<GeoConstruct>list = new LinkedList<GeoConstruct>();
	protected LinkedList<GeoConstruct> clickedList =new LinkedList<GeoConstruct>(),
										futureList =new LinkedList<GeoConstruct>();
	protected GeoConstruct potentialClick=null, lastPotential=null, fixedObject=null, trackPoint=null;
	protected double[]	vector1=  {0,0,0}, vector2={0,0,0},
	norm=     {0,0,0},  binorm={0,0,0},
	dragStart={0,0,0}, dragNow={0,0,0};
	GeoCanvas(){
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	GeoCanvas(GeoMetry m){
		this();
		geometry=m;
	}

	public void paint(Graphics g) {
		if (GeoPlayground.canvas[GeoPlayground.tabbedPanePlane.getSelectedIndex()].getWidth()<550)
			for (int i=0;i<7;i++) GeoPlayground.tabbedPanePlane.setTitleAt(i,GeoPlayground.tabText[1][i]);
		else for (int i=0;i<7;i++) GeoPlayground.tabbedPanePlane.setTitleAt(i,GeoPlayground.tabText[0][i]);
		SZ=(GeoPlayground.getHght()-1)/2;
		geometry.drawModel(g, SZ);
		
/*	  int l=0;																//
      for (int i=0;i<list.size();i++) {										// This was used
	  if (list.get(i).getShown()) {											// for debugging
	  g.setColor(new Color(127,127,255));									// purposes, and
	  if (list.get(i).getType()<0) g.setColor(new Color(255,127,127));		// remains for
	  else if (list.get(i).getType()==0) g.setColor(new Color(127,191,127));// possible future
	     //list.get(i).getXYZ(vector1);										// use for same.
	     //for (int j=0;j<3;j++) vector2[j]=0;								//
	     //if (i>0) list.get(i-1).getXYZ(vector2);							//
		 //double phi=MathEqns.chop(MathEqns.dotProduct(vector1,vector2));	//
	  if (list.get(i).getValid()) 											//
	  g.drawString(""+i+":"+MathEqns.chop(list.get(i).getX())+" "			//
	                 +MathEqns.chop(list.get(i).getY())+" "					//
					 +MathEqns.chop(list.get(i).getZ())+" , "				//
					 +list.get(i).getSize()+" , "+list.get(i).getType(),	//
					                        5+SZ*(l/19),13+25*l-475*(l/19));//
	  else g.drawString("----- ----- -----",5+SZ*(l/19),13+25*l-475*(l/19));//
	  if (list.get(i).getValidNew())										//
	  g.drawString(""+i+":"+MathEqns.chop(list.get(i).getNewX())+" "		//
					 +MathEqns.chop(list.get(i).getNewY())+" "				//
					 +MathEqns.chop(list.get(i).getNewZ())+" "//+phi		//
					                       ,5+SZ*(l/19),25+25*l-475*(l/19));//
	  else g.drawString("----- ----- -----",5+SZ*(l/19),25+25*l-475*(l/19));//
	  g.drawString(""+GeoPlayground.whatToDo,2*SZ-15,2*SZ-15);				//
	  l++;																	//
	  }																		//
	} //*/																	//
		if (geometry.getScale()!=1.0) {
	    g.setColor(Color.black);
	    String textString=""+((int)MathEqns.chop(geometry.getScale()));
	    if (geometry.getScale()<1) textString="1/"+((int)MathEqns.chop(1/geometry.getScale()));
	    g.drawString("("+textString+")",5,15);
	  }
	  drawAllConstructs(g,false);		// draw the Constructs
	  switch (GeoPlayground.whatToDo){  // look at what the user is doing
		case GeoPlayground.Move:
			if (!clickedList.isEmpty()) {																//
				boolean isNew=false;																	// 01/30/2010
				LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();						// we only want
				tempList.add(clickedList.get(0));														// to highlight
				for (int i=clickedList.get(0).getID()+1;i<list.size();i++) {							// moved
					for(int j=0;j<tempList.size();j++)													// constructs,
						if(list.get(i).constList.contains(tempList.get(j)))								// hence this
							{tempList.add(list.get(i));break;}											// hack fix,
				}																						// converted
				for (int i=0;i<list.size();i++) {														// from drawAll,
					isNew=false;																		// only
					if (tempList.contains(list.get(i))) isNew=true;										// highlights
					if ((list.get(i).getValidNew() && isNew) ||											// the stuff
						(list.get(i).getValid() && !isNew))												// that is in
					if (list.get(i).getShown())															// tempList,
					if (list.get(i).getType()>=30 && isNew) {	// measures are hard to read when the	// i.e., the
						double[] v0={0,0,0}, v1={0,0,0};		// new measure is atop the old, hence	// stuff that
						list.get(i).getXYZ(v0);					//										// has ancestor
						list.get(i).getNewXYZ(v1);				// if the measure has changed			// the moved
						if (MathEqns.norm(v0,v1)<.01) {			// but its placement hasn't moved		// point.
							g.setColor(Color.white);			// we erase								//
							list.get(i).draw(g,SZ,false);		// the old measure						//
						}										//										//
						colorSet(g,list.get(i),isNew);			// we draw the							//
						list.get(i).draw(g,SZ,isNew);			// new measure							//
					}											//										//
					else if (list.get(i).getType()>0){													//
						colorSet(g,list.get(i),isNew);													//
						list.get(i).draw(g,SZ,isNew);													//
					}																					//
				}																						//
				for (int i=0;i<list.size();i++)	{														//
					isNew=false;
					if (tempList.contains(list.get(i))) isNew=true;
					if ((list.get(i).getValidNew() && isNew) ||											//
							(list.get(i).getValid() && !isNew))											//
					if (list.get(i).getShown()) 														//
					if (list.get(i).getType()<=0){														//
						colorSet(g,list.get(i),isNew);													//
						list.get(i).draw(g,SZ,isNew);													//
					}																					//
				}																						//
			}																							//
			else if (potentialClick!=null) {colorSet(g,potentialClick,true);potentialClick.draw(g,SZ,true);}
			else if (MathEqns.norm(dragStart,dragNow)>0)
				drawAllConstructs(g,true);
			if (trackPoint!=null) {
				g.setColor(new Color(220,0,220));
				trackPoint.draw(g, SZ, true);
			}
			break;
		default:
		case GeoPlayground.TrackObject:
		case GeoPlayground.MeasureSum:
		case GeoPlayground.MeasureDiff:
		case GeoPlayground.MeasureProd:
		case GeoPlayground.MeasureRatio:
		case GeoPlayground.MakeLines:
		case GeoPlayground.MakeSegment:
		case GeoPlayground.MakeRays:
		case GeoPlayground.MakeCircles:
		case GeoPlayground.MakeInt:
		case GeoPlayground.MakePoints:
		case GeoPlayground.MakePerps:
		case GeoPlayground.MakeParlls:
		case GeoPlayground.MakeMdPt:
		case GeoPlayground.HideObject:
		case GeoPlayground.LabelObject:
		case GeoPlayground.FixObject:
		case GeoPlayground.MeasureDist:
		case GeoPlayground.MeasureArea:
		case GeoPlayground.MeasureCirc:
		case GeoPlayground.MakeBisect:
		case GeoPlayground.MeasureTri:
		case GeoPlayground.MeasureAngle:
		case GeoPlayground.ReflectPt:
		case GeoPlayground.RotatePt:
		case GeoPlayground.TranslatePt:
		case GeoPlayground.InvertPt:
		case GeoPlayground.MakeFxPt:
		case GeoPlayground.MeasureCnst:
			for (int i=0;i<clickedList.size();i++) {
			  colorSet(g,clickedList.get(i),true);
			  clickedList.get(i).draw(g,SZ,true);
			}
			if (potentialClick!=null) {colorSet(g,potentialClick,true);potentialClick.draw(g,SZ,true);}
			for(int i=clickedList.size();i<futureList.size();i++)
				{colorSet(g,futureList.get(i),true);if (futureList.get(i).getShown()) futureList.get(i).draw(g,SZ,true);}
			break;
		}
		if (fixedObject!=null) {
			if (fixedObject!=potentialClick) g.setColor(Color.gray);
			else g.setColor(new Color(220,220,0));
			fixedObject.draw(g,SZ,false);
		}
		if (trackPoint!=null) {
			if (trackPoint!=potentialClick) g.setColor(new Color(127,0,127));
			else g.setColor(new Color(220,0,220));
			trackPoint.draw(g,SZ,false);
		}
		if (geometry.getGeometry()==2 && GeoPlayground.model==2) {
			g.setColor(Color.white);
			int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
			if (fudge<0) fudge=0;
			g.fillOval(SZ-1+fudge,SZ-1,3,3);
		}
	}
	
	public void colorSet(Graphics g, GeoConstruct a, boolean isNew) {
		if (isNew && a.getType()<0) g.setColor(new Color(175,175,255));
		else if (a.getType()<0) g.setColor(Color.blue);
		else if (isNew && a.getType()==0) g.setColor(new Color(127,223,127));
		else if (a.getType()==0) g.setColor(new Color(0,127,0));
		else if (isNew && a.getType()<30) g.setColor(new Color(255,127,127));
		else if (a.getType()<30) g.setColor(Color.red);
		else if (isNew) g.setColor(Color.gray);
		else g.setColor(Color.black);
	}
	public void drawAllConstructs(Graphics g, boolean isNew){
		for (int i=0;i<list.size();i++)
			if ((list.get(i).getValidNew() && isNew) ||
				(list.get(i).getValid() && !isNew))
			if (list.get(i).getShown())
			if (list.get(i).getType()>=30 && isNew) {	// measures are hard to read when the
				double[] v0={0,0,0}, v1={0,0,0};		// new measure is atop the old, hence
				list.get(i).getXYZ(v0);					//
				list.get(i).getNewXYZ(v1);				// if the measure has changed
				if (MathEqns.norm(v0,v1)<.01) {			// but its placement hasn't moved
					g.setColor(new Color(230,230,230));	// we erase
					list.get(i).draw(g,SZ,false);		// the old measure
				}										//
				colorSet(g,list.get(i),isNew);			// we draw the
				list.get(i).draw(g,SZ,isNew);			// new measure
			}											//
			else if (list.get(i).getType()>0){
				colorSet(g,list.get(i),isNew);
				list.get(i).draw(g,SZ,isNew);
			}
		for (int i=0;i<list.size();i++)
			if ((list.get(i).getValidNew() && isNew) ||
					(list.get(i).getValid() && !isNew))
			if (list.get(i).getShown()) 
			if (list.get(i).getType()<=0){
				colorSet(g,list.get(i),isNew);
				list.get(i).draw(g,SZ,isNew);
			}
		
	}
	public void translateAll() {
		for (int i=0;i<list.size();i++)
			if(list.get(i).getType()>0)
				list.get(i).translate(dragStart,dragNow);
			else list.get(i).update();
	}
	public void getPotentialAny(double[] v1){
		potentialClick=null;
		getPotentialPointOrInt(v1);
		if (potentialClick==null) getPotentialComposite(v1);
	}
	public void getPotentialComposite(double[] v1){
		potentialClick=null;
		int lastFound=-1;
		for (int i=0;i<list.size();i++)
			if (list.get(i).getType()<=0)
				if (list.get(i).getShown() && list.get(i).getValid()) 
					if (!clickedList.contains(list.get(i))) {
						if ((list.get(i).mouseIsOver(v1,SZ)) && !clickedList.contains(list.get(i))){
							potentialClick=list.get(i);
							if ((potentialClick.getType()==SEGMENT || potentialClick.getType()==RAY) && geometry.getGeometry()!=6){// this prevents
								LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();   // segments and
								double[] v0={0,0,0};												// rays from being
								tempList.add(potentialClick);										// chosen from
								GeoConstruct temp=geometry.createPoint(PTonLINE,tempList,v1);		// outside their
								temp.update(); temp.getNewXYZ(v0);									// domain
								if (MathEqns.norm(v0,v1)>.03/geometry.getScale()) 
									if (lastFound>-1) potentialClick=list.get(lastFound);
									else potentialClick=null;
							}
							else lastFound=i;
						}
					}
	}
	public void getPotentialDistance(double[] v1) {
		potentialClick=null;
		for (int i=list.size()-1;i>=0;i--)
			if (list.get(i).getType()==DISTANCE)
				if (list.get(i).getShown() && list.get(i).getValid()) 
					if (!clickedList.contains(list.get(i))) {
						if ((list.get(i).mouseIsOver(v1,SZ)) && !clickedList.contains(list.get(i))){
							potentialClick=list.get(i);
						}
					}
	}
	public void getPotentialAngle(double[] v1) {
		potentialClick=null;
		for (int i=list.size()-1;i>=0;i--)
			if (list.get(i).getType()==ANGLE)
				if (list.get(i).getShown() && list.get(i).getValid()) 
					if (!clickedList.contains(list.get(i))) {
						if ((list.get(i).mouseIsOver(v1,SZ)) && !clickedList.contains(list.get(i))){
							potentialClick=list.get(i);
						}
					}
	}
	public void getPotentialLine(double[] v1){
		potentialClick=null;
		int lastFound=-1;
		for (int i=list.size()-1;i>=0;i--)
			if (list.get(i).getType()<0)
				if (list.get(i).getShown() && list.get(i).getValid()) 
					if (!clickedList.contains(list.get(i))) {
						if ((list.get(i).mouseIsOver(v1,SZ)) && !clickedList.contains(list.get(i))){
							potentialClick=list.get(i);
							if ((potentialClick.getType()==SEGMENT || potentialClick.getType()==RAY) && geometry.getGeometry()!=6){// this prevents
								LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();   // segments and
								double[] v0={0,0,0};												// rays from being
								tempList.add(potentialClick);										// chosen from
								GeoConstruct temp=geometry.createPoint(PTonLINE,tempList,v1);		// outside their
								temp.update(); temp.getNewXYZ(v0);									// domain
								if (MathEqns.norm(v0,v1)>.03/geometry.getScale())
									if (lastFound>-1) potentialClick=list.get(lastFound);
							}
						}
						else lastFound=i;
					}
	}
	public void getPotentialCircle(double[] v1){
		potentialClick=null;
		for (int i=0;i<list.size();i++)
			if (list.get(i).getType()==0)
				if (list.get(i).getShown()) 
					if (!clickedList.contains(list.get(i)) && list.get(i).getValid())
						if ((list.get(i).mouseIsOver(v1,SZ)) && !clickedList.contains(list.get(i)))
								potentialClick=list.get(i);
	}
	public void getPotentialPointOrMeasure(double[] v1){
		potentialClick=null;
		for (int i=0;i<list.size();i++)
			if (list.get(i).getType()==POINT ||
				list.get(i).getType()==PTonLINE ||
				list.get(i).getType()==PTonCIRC ||
				list.get(i).getType()>30)
				if (list.get(i).getShown() && list.get(i).getValid())
					if (list.get(i).mouseIsOver(v1, SZ)) 
						if (!clickedList.contains(list.get(i)))
							potentialClick=list.get(i);
	}
	public void getPotentialPointOrInt(double[] v1) {
		potentialClick=null;
		for (int i=0;i<list.size();i++)
			if (list.get(i).getType()>0 && list.get(i).getType()<30)
				if (list.get(i).getShown() && list.get(i).getValid()) 
					if (!clickedList.contains(list.get(i)))
						if (list.get(i).mouseIsOver(v1, SZ))
							potentialClick=list.get(i);
	}
	public void getPotentialPointOrIntOrMeasure(double[] v1) {
		potentialClick=null;
		for (int i=0;i<list.size();i++)
			if (list.get(i).getType()>0)
				if (list.get(i).getShown() && list.get(i).getValid())
					if (list.get(i).mouseIsOver(v1, SZ)) 
						if (!clickedList.contains(list.get(i)))
							potentialClick=list.get(i);
	}
	public void getPotentialMeasure(double[] v1) {
		potentialClick=null;
		for (int i=0;i<list.size();i++)
			if (list.get(i).getType()>30)
				if (list.get(i).getShown() && list.get(i).getValid())
					if (list.get(i).mouseIsOver(v1,SZ))
						if (!clickedList.contains(list.get(i)))
							potentialClick=list.get(i);
	}
	public void addToList(LinkedList<GeoConstruct>thisList) {
		if (potentialClick==null) {
			LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
			temp.clear();
			thisList.add(geometry.createPoint(GeoConstruct.POINT,temp,vector1));
			thisList.getLast().setID(-1);
			thisList.getLast().update();
		}
		else if (potentialClick.getType()==0) {
			LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
			temp.add(potentialClick);
			thisList.add(geometry.createPoint(GeoConstruct.PTonCIRC,temp,vector1));
			thisList.getLast().setID(-1);
			thisList.getLast().update();
		}
		else if (potentialClick.getType()<0) {
			LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
			temp.add(potentialClick);
			thisList.add(geometry.createPoint(GeoConstruct.PTonLINE,temp,vector1));
			thisList.getLast().setID(-1);
			thisList.getLast().update();
		}
		else thisList.add(potentialClick);
		thisList.getLast().getNewXYZ(vector1);
		thisList.getLast().setXYZ(vector1);
	}
	public void updateNewStuff(LinkedList<GeoConstruct>thisList) {
		if (thisList==list) thisList.getLast().setID(thisList.size()-1);
		thisList.getLast().update();
		thisList.getLast().getNewXYZ(vector1);
		thisList.getLast().setXYZ(vector1);
	}
	public void doGetPotential() {
		getPotentialPointOrInt(vector1);
		if (clickedList.size()>0 && potentialClick!=null && geometry.getGeometry()<2) {
		  clickedList.getLast().getXYZ(vector1);				// this stops the user
		  potentialClick.getXYZ(vector2);						// from creating lines
		  double dist=0;										// between antipodes in
		  for (int j=0;j<3;j++) dist+=(vector1[j]+vector2[j]);	// S (&P)
		  if (Math.abs(dist)<.0001) potentialClick=null;		//
		}
		if (potentialClick==null) getPotentialComposite(vector1);
		addToList(futureList);
	}
	public void addChangeListener(ChangeListener e) {geometryListener=e;}

	public void mousePressed(MouseEvent arg0) {
		vector1=geometry.convertMousetoCoord(arg0,SZ);
		Cursor fingerCursor = new Cursor(Cursor.HAND_CURSOR),
		defaultCursor= new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(defaultCursor); 
		if(geometry.mouseIsValid(vector1)) {
			alreadyMoving=true;
			switch(GeoPlayground.whatToDo){ 
			case GeoPlayground.Move:
				for (int i=0;i<3;i++) dragStart[i]=vector1[i];
				if(potentialClick!=null){
					if(geometry.mouseIsValid(vector1))
						setCursor(fingerCursor); 
					clickedList.clear();
					clickedList.add(potentialClick);
				}
				break;
			}
		}
		repaint();
	}

	public void mouseMoved(MouseEvent arg0) {// if the mouse is moved
		alreadyMoving=false;
		vector1=geometry.convertMousetoCoord(arg0,SZ);
		Cursor defaultCursor= new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(defaultCursor);
		futureList.clear();
		for (int i=0;i<clickedList.size();i++) futureList.addLast(clickedList.get(i));
		if (geometry.mouseIsValid(vector1)) switch (GeoPlayground.whatToDo) {
		case GeoPlayground.TrackObject:
			getPotentialPointOrInt(vector1);
			break;
		case GeoPlayground.FixObject:
			getPotentialAny(vector1);
			break;
		case GeoPlayground.MakeCircles:
			doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createCircle(CIRCLE,futureList));
			break;
		case GeoPlayground.MakeFxPt:
			futureList.clear();
			futureList.addLast(geometry.createPoint(FIXedPT,futureList,vector1));
			break;
		case GeoPlayground.MeasureCnst:
			futureList.clear();
			futureList.addLast(geometry.createPoint(CONSTANT,futureList,vector1));
			break;
		case GeoPlayground.MakeComment:
			futureList.clear();
			futureList.addLast(geometry.createPoint(COMMENT,futureList,vector1));
			break;
		case GeoPlayground.MakeMdPt:
			doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createPoint(MIDPT,futureList,vector1));
			break;
		case GeoPlayground.MakeBisect:
			doGetPotential();
			if (futureList.size()==3) futureList.addLast(geometry.createLine(BISECTOR,futureList));
			updateNewStuff(futureList);
			break;
		case GeoPlayground.MakeLines:
			doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createLine(LINE,futureList));
			break;
		case GeoPlayground.MakeSegment:
			doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createLine(SEGMENT,futureList));
			break;
		case GeoPlayground.MakeRays:
			doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createLine(RAY,futureList));
			updateNewStuff(futureList);
			break;
		case GeoPlayground.MeasureDist:
			doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createPoint(DISTANCE,futureList,vector1));
			break;
		case GeoPlayground.MeasureTri:
			doGetPotential();
			if (futureList.size()==3) futureList.addLast(geometry.createPoint(TRIANGLE,futureList,vector1));
			break;
		case GeoPlayground.MeasureAngle:
			doGetPotential();
			if (futureList.size()==3) futureList.addLast(geometry.createPoint(ANGLE,futureList,vector1));
			break;
		case GeoPlayground.MakePoints:
			doGetPotential();
			break;
		case GeoPlayground.MakeInt:
			getPotentialComposite(vector1);
			break;
		case GeoPlayground.MeasureArea:
			getPotentialCircle(vector1);
			if (potentialClick!=null) {
				for (int i=0;i<2;i++) futureList.addLast(potentialClick.get(i));
				futureList.addLast(potentialClick);
				futureList.addLast(geometry.createPoint(AREA,futureList,vector1));
			}
			break;
		case GeoPlayground.MeasureCirc:
			getPotentialCircle(vector1);
			if (potentialClick!=null) {
				for (int i=0;i<2;i++) futureList.addLast(potentialClick.get(i));
				futureList.addLast(potentialClick);
				futureList.addLast(geometry.createPoint(CIRCUMF,futureList,vector1));
			}
			break;
		case GeoPlayground.MeasureSum:
			getPotentialMeasure(vector1);
			if (clickedList.size()>0 && potentialClick!=null) {
				futureList.addLast(potentialClick);
				futureList.addLast(geometry.createPoint(SUM,futureList,vector1));
			}
			break;
		case GeoPlayground.MeasureDiff:
			getPotentialMeasure(vector1);
			if (clickedList.size()>0 && potentialClick!=null) {
				futureList.addLast(potentialClick);
				futureList.addLast(geometry.createPoint(DIFF,futureList,vector1));
			}
			break;
		case GeoPlayground.MeasureProd:
			getPotentialMeasure(vector1);
			if (clickedList.size()>0 && potentialClick!=null) {
				futureList.addLast(potentialClick);
				futureList.addLast(geometry.createPoint(PROD,futureList,vector1));
			}
			break;
		case GeoPlayground.MeasureRatio:
			getPotentialMeasure(vector1);
			if (clickedList.size()>0 && potentialClick!=null) {
				futureList.addLast(potentialClick);
				futureList.addLast(geometry.createPoint(RATIO,futureList,vector1));
			}
			break;
		case GeoPlayground.MakePerps:
			if (clickedList.size()==0) getPotentialLine(vector1);
			else {
				getPotentialPointOrInt(vector1);
				if (clickedList.size()>0 && potentialClick!=null && geometry.getGeometry()<2) {
				  clickedList.getLast().getXYZ(vector1);				// this stops the user
				  potentialClick.getXYZ(vector2);						// from creating lines
				  double dist=0;										// between antipodes in
				  for (int j=0;j<3;j++) dist+=(vector1[j]+vector2[j]);	// S (&P)
				  if (Math.abs(dist)<.0001) potentialClick=null;		//
				}
				if (potentialClick==null) 
					if (clickedList.get(0).mouseIsOver(vector1,SZ))
							potentialClick=clickedList.get(0);
					else getPotentialComposite(vector1);
				addToList(futureList);
			}
			if (futureList.size()==2) futureList.addLast(geometry.createLine(PERP,futureList));
			if (futureList.size()==3) updateNewStuff(futureList);
			break;
		case GeoPlayground.MakeParlls:
			if (clickedList.size()==0) getPotentialLine(vector1);
			else doGetPotential();
			if (futureList.size()==2) {
				futureList.addLast(geometry.createLine(PARLL0,futureList));
				futureList.getLast().update();
				futureList.getLast().getNewXYZ(vector2);
				futureList.getLast().setXYZ(vector2);
			}
			if (geometry.getGeometry()==3 && futureList.size()==3) {
				LinkedList<GeoConstruct> temp = new LinkedList<GeoConstruct>();
				temp.add(futureList.get(0));	temp.add(futureList.get(1));
				futureList.addLast(geometry.createLine(PARLL1,temp));
				futureList.getLast().update();
				futureList.getLast().getNewXYZ(vector2);
				futureList.getLast().setXYZ(vector2);
			}
			break;
		case GeoPlayground.ReflectPt:
			if (clickedList.size()==0) getPotentialLine(vector1);
			else doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createPoint(REFLECT_PT,futureList,vector1));
			break;
		case GeoPlayground.RotatePt:
			if (clickedList.size()==0) getPotentialAngle(vector1);
			else doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createPoint(ROTATE_PT,futureList,vector1));
			break;
		case GeoPlayground.InvertPt:
			if (clickedList.size()==0) getPotentialCircle(vector1);
			else doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createPoint(INVERT_PT,futureList,vector1));
			break;
		case GeoPlayground.TranslatePt:
			if (clickedList.size()==0) getPotentialDistance(vector1);
			else doGetPotential();
			if (futureList.size()==2) futureList.addLast(geometry.createPoint(TRANSLATE_PT,futureList,vector1));
			break;
		case GeoPlayground.Move:
			if (trackPoint==null) getPotentialPointOrMeasure(vector1);
			else if (fixedObject==null) getPotentialPointOrMeasure(vector1);
			else potentialClick=null;
			break;
		case GeoPlayground.LabelObject:
			getPotentialPointOrIntOrMeasure(vector1);
			if (potentialClick==null) getPotentialComposite(vector1);
			break;
		case GeoPlayground.HideObject:
			getPotentialPointOrIntOrMeasure(vector1);
			if (potentialClick==null) getPotentialComposite(vector1);
			break;
		}
		else potentialClick=null;
		if (potentialClick!=lastPotential) {
			lastPotential=potentialClick;
		}
		repaint();
	} 

	public void mouseDragged(MouseEvent arg0) {// if the mouse is dragged
		potentialClick=null;
		vector1=geometry.convertMousetoCoord(arg0,SZ);
		Cursor fingerCursor = new Cursor(Cursor.HAND_CURSOR),
		defaultCursor= new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(defaultCursor);
		if (geometry.mouseIsValid(vector1)) switch (GeoPlayground.whatToDo) {
		case GeoPlayground.Move:
			if (fixedObject==null) {
				if (clickedList.size()>0) {
					clickedList.get(0).setNewXYZ(vector1);
					clickedList.get(0).update();
					LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();
					tempList.add(clickedList.get(0));
					for (int i=clickedList.get(0).getID()+1;i<list.size();i++) {
					  for(int j=0;j<tempList.size();j++)
						  if(list.get(i).constList.contains(tempList.get(j)))
							 {tempList.add(list.get(i));break;}
					}
					double[] temp00={0,0,0},temp01={0,0,0},temp10={0,0,0},temp11={0,0,0},
							 temp02={0,0,0},temp03={0,0,0},temp12={0,0,0},temp13={0,0,0};
					for (int i=0;i<tempList.size();i++) {
						boolean nowValid=true;
						for (int j=0;j<tempList.get(i).getSize();j++) {
							nowValid=(nowValid && tempList.get(i).get(j).getValidNew());
						}
						if (nowValid) { // before updating, we save the coordinates of any points of
							// intersection.  After updating, it is possible that we have crossed a
							// branch cut, and the two (or four) roots have transposed.  Hence, we
							// check to see which coordinates are closest to the previous coordinates,
							// and switch them if necessary.  This is the fix to what was called the
							// "branch cut bug."  The projective CIRCintCIRC case, as can be seen,
							// requires special attention. 2009_05_09 djh
							tempList.get(i).setValidNew(true);
							if (tempList.get(i).getType()==CIRCintCIRC00) tempList.get(i).getNewXYZ(temp00);
							if (tempList.get(i).getType()==CIRCintCIRC01) tempList.get(i).getNewXYZ(temp01);
							if (tempList.get(i).getType()==CIRCintCIRC10) tempList.get(i).getNewXYZ(temp02);
							if (tempList.get(i).getType()==CIRCintCIRC11) tempList.get(i).getNewXYZ(temp03);
							if (tempList.get(i).getType()==CIRCintLINE0) tempList.get(i).getNewXYZ(temp00);
							if (tempList.get(i).getType()==CIRCintLINE1) tempList.get(i).getNewXYZ(temp01);
							if (tempList.get(i).getType()==LINEintLINE0) tempList.get(i).getNewXYZ(temp00);
							if (tempList.get(i).getType()==LINEintLINE1) tempList.get(i).getNewXYZ(temp01);
							tempList.get(i).update();
							if (tempList.get(i).getType()==CIRCintCIRC00) tempList.get(i).getNewXYZ(temp10);
							if (tempList.get(i).getType()==CIRCintCIRC01) tempList.get(i).getNewXYZ(temp11);
							if (tempList.get(i).getType()==CIRCintCIRC10) tempList.get(i).getNewXYZ(temp12);
							if (tempList.get(i).getType()==CIRCintCIRC11) tempList.get(i).getNewXYZ(temp13);
							if (tempList.get(i).getType()==CIRCintLINE0) tempList.get(i).getNewXYZ(temp10);
							if (tempList.get(i).getType()==CIRCintLINE1) tempList.get(i).getNewXYZ(temp11);
							if (tempList.get(i).getType()==LINEintLINE0) tempList.get(i).getNewXYZ(temp10);
							if (tempList.get(i).getType()==LINEintLINE1) tempList.get(i).getNewXYZ(temp11);
							if (((tempList.get(i).getType()==CIRCintCIRC01 && geometry.getGeometry()!=1) ||
								 tempList.get(i).getType()==CIRCintLINE1 ||
								 tempList.get(i).getType()==LINEintLINE1) &&
								 MathEqns.norm(temp00,temp10)+MathEqns.norm(temp01,temp11)>
								 MathEqns.norm(temp00,temp11)+MathEqns.norm(temp01,temp10)) {
								tempList.get(i).setNewXYZ(temp10);
								tempList.get(i-1).setNewXYZ(temp11);
								boolean bit = tempList.get(i).getValidNew();
								tempList.get(i).setValidNew(tempList.get(i-1).getValidNew());
								tempList.get(i-1).setValidNew(bit);
							}
							if (tempList.get(i).getType()==CIRCintCIRC11) {
								int permute=0;
								double currentDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp10)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp11)))+
													   MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp12)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp13)));
								double permuteDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp11)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp10)))+
								   					   MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp12)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp13)));
								if (currentDistance>permuteDistance) {currentDistance=permuteDistance;permute=1;}
								permuteDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp10)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp11)))+
			   					   				MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp13)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp12)));
								if (currentDistance>permuteDistance) {currentDistance=permuteDistance;permute=2;}
								permuteDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp11)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp10)))+
					   							MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp13)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp12)));
								if (currentDistance>permuteDistance) {currentDistance=permuteDistance;permute=3;}
								permuteDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp12)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp13)))+
	   											MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp10)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp11)));
								if (currentDistance>permuteDistance) {currentDistance=permuteDistance;permute=4;}
								permuteDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp13)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp12)))+
			   					   				MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp10)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp11)));
								if (currentDistance>permuteDistance) {currentDistance=permuteDistance;permute=5;}
								permuteDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp12)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp13)))+
					   							MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp11)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp10)));
								if (currentDistance>permuteDistance) {currentDistance=permuteDistance;permute=6;}
								permuteDistance=MathEqns.min(Math.acos(MathEqns.dotProduct(temp00,temp13)),Math.PI-Math.acos(MathEqns.dotProduct(temp00,temp13)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp01,temp12)),Math.PI-Math.acos(MathEqns.dotProduct(temp01,temp12)))+
	   											MathEqns.min(Math.acos(MathEqns.dotProduct(temp02,temp11)),Math.PI-Math.acos(MathEqns.dotProduct(temp02,temp11)))+MathEqns.min(Math.acos(MathEqns.dotProduct(temp03,temp10)),Math.PI-Math.acos(MathEqns.dotProduct(temp03,temp10)));
								if (currentDistance>permuteDistance) {currentDistance=permuteDistance;permute=7;}
								boolean bit;
								switch (permute) {
								case 0:
									//tempList.get(i-0).setNewXYZ(temp13); tempList.get(i-1).setNewXYZ(temp12);
									//tempList.get(i-2).setNewXYZ(temp11); tempList.get(i-3).setNewXYZ(temp10);
									break;
								case 1:
									//tempList.get(i-0).setNewXYZ(temp13); tempList.get(i-1).setNewXYZ(temp12);
									tempList.get(i-2).setNewXYZ(temp10); tempList.get(i-3).setNewXYZ(temp11);
									bit=tempList.get(i-2).getValidNew();
									tempList.get(i-2).setValidNew(tempList.get(i-3).getValidNew());
									tempList.get(i-3).setValidNew(bit);
									break;
								case 2:
									tempList.get(i-0).setNewXYZ(temp12); tempList.get(i-1).setNewXYZ(temp13);
									bit=tempList.get(i-0).getValidNew();
									tempList.get(i-0).setValidNew(tempList.get(i-1).getValidNew());
									tempList.get(i-1).setValidNew(bit);
									//tempList.get(i-2).setNewXYZ(temp11); tempList.get(i-3).setNewXYZ(temp10);
									break;
								case 3:
									tempList.get(i-0).setNewXYZ(temp12); tempList.get(i-1).setNewXYZ(temp13);
									tempList.get(i-2).setNewXYZ(temp10); tempList.get(i-3).setNewXYZ(temp11);
									bit=tempList.get(i-0).getValidNew();
									tempList.get(i-0).setValidNew(tempList.get(i-1).getValidNew());
									tempList.get(i-1).setValidNew(bit);
									bit=tempList.get(i-2).getValidNew();
									tempList.get(i-2).setValidNew(tempList.get(i-3).getValidNew());
									tempList.get(i-3).setValidNew(bit);
									break;
								case 4:
									tempList.get(i-0).setNewXYZ(temp11); tempList.get(i-1).setNewXYZ(temp10);
									tempList.get(i-2).setNewXYZ(temp13); tempList.get(i-3).setNewXYZ(temp12);
									bit=tempList.get(i-1).getValidNew();
									tempList.get(i-1).setValidNew(tempList.get(i-3).getValidNew());
									tempList.get(i-3).setValidNew(bit);
									bit=tempList.get(i-2).getValidNew();
									tempList.get(i-2).setValidNew(tempList.get(i-0).getValidNew());
									tempList.get(i-0).setValidNew(bit);
									break;
								case 5:
									tempList.get(i-0).setNewXYZ(temp11); tempList.get(i-1).setNewXYZ(temp10);
									tempList.get(i-2).setNewXYZ(temp12); tempList.get(i-3).setNewXYZ(temp13);
									bit=tempList.get(i-0).getValidNew();
									tempList.get(i-0).setValidNew(tempList.get(i-2).getValidNew());
									tempList.get(i-2).setValidNew(tempList.get(i-1).getValidNew());
									tempList.get(i-1).setValidNew(tempList.get(i-3).getValidNew());
									tempList.get(i-3).setValidNew(bit);
									break;
								case 6:
									tempList.get(i-0).setNewXYZ(temp10); tempList.get(i-1).setNewXYZ(temp11);
									tempList.get(i-2).setNewXYZ(temp13); tempList.get(i-3).setNewXYZ(temp12);
									bit=tempList.get(i-0).getValidNew();
									tempList.get(i-0).setValidNew(tempList.get(i-3).getValidNew());
									tempList.get(i-3).setValidNew(tempList.get(i-1).getValidNew());
									tempList.get(i-1).setValidNew(tempList.get(i-2).getValidNew());
									tempList.get(i-2).setValidNew(bit);
									break;
								case 7:
									tempList.get(i-0).setNewXYZ(temp10); tempList.get(i-1).setNewXYZ(temp11);
									tempList.get(i-2).setNewXYZ(temp12); tempList.get(i-3).setNewXYZ(temp13);
									bit=tempList.get(i-0).getValidNew();
									tempList.get(i-0).setValidNew(tempList.get(i-3).getValidNew());
									tempList.get(i-3).setValidNew(bit);
									bit=tempList.get(i-1).getValidNew();
									tempList.get(i-1).setValidNew(tempList.get(i-2).getValidNew());
									tempList.get(i-2).setValidNew(bit);
									break;
								}
								
							}
						}
						else tempList.get(i).setValidNew(false);
					}
				}
				else if (alreadyMoving){
					setCursor(fingerCursor);
				for (int i=0;i<3;i++) dragNow[i]=vector1[i];
				boolean axisNotExist=true;
				for (int i=0;i<3;i++) if (dragStart[i]!=dragNow[i]) axisNotExist=false;
				if (!axisNotExist) translateAll();
				break;
				}
			}
			else {// fixedObject
				for (int i=0;i<3;i++) dragNow[i]=vector1[i];
				for (int i=0;i<list.size();i++)
					if (list.get(i).getType()>0)
						list.get(i).transform(fixedObject,dragStart,dragNow);
				for (int i=0;i<list.size();i++)
					if (list.get(i).getType()<=0)
						list.get(i).update();
			}
		}
		else {
			for (int i=0;i<list.size();i++) {
				list.get(i).getXYZ(vector2);
				list.get(i).setNewXYZ(vector2);
			}
		}
		if (trackPoint!=null) {
			boolean alreadyExists=false;
			for (int i=0;i<list.size();i++)
				if (list.get(i).getType()==FIXedPT) {
					list.get(i).getNewXYZ(vector2);
					trackPoint.getNewXYZ(vector1);
					if (MathEqns.norm(vector1,vector2)<0.05/geometry.getScale()) {
						alreadyExists=true;
						break;
					}
				}
			if (!alreadyExists && trackPoint.getValidNew()) {
				LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();
				tempList.add(trackPoint);
				trackPoint.getNewXYZ(vector1);
				list.addLast(geometry.createPoint(GeoConstruct.FIXedPT,tempList,vector1));
				list.getLast().setID(list.size()-1);
				//list.getLast().setXYZ(vector1);
			}
		}
		repaint();
	}

	public void mouseReleased(MouseEvent arg0) {// if the mouse is released
		alreadyMoving=false;
		vector1=geometry.convertMousetoCoord(arg0,SZ);
		Cursor defaultCursor= new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(defaultCursor);
		if (geometry.mouseIsValid(vector1)) switch (GeoPlayground.whatToDo) {
		case GeoPlayground.Move:
			for (int i=0;i<list.size();i++) {
				if (list.get(i).getValidNew()) {
				  list.get(i).getNewXYZ(vector2);
				  list.get(i).setXYZ(vector2);
				}
				list.get(i).setValid(list.get(i).getValidNew());
			}
			clickedList.clear();
			break;			
		}
		else{
			potentialClick=null;
			clickedList.clear();
			for (int i=0;i<list.size();i++) {
				list.get(i).getXYZ(vector2);
				list.get(i).setNewXYZ(vector2);
			}
		}
		for (int i=0;i<3;i++) {dragStart[i]=dragNow[i];}
		repaint();
	}

	public void mouseEntered(MouseEvent arg0) {}// if the mouse enters

	public void mouseExited(MouseEvent arg0) {// if the mouse exits
		for (int i=0;i<list.size();i++) {
			list.get(i).getXYZ(vector1);
			list.get(i).setNewXYZ(vector1);
		}
		alreadyMoving=false;
		futureList.clear();			//
		potentialClick=null;        // anything it was doing is dropped
		clickedList.clear();        // 
		repaint();                  // the screen is repainted.
	} 

	public void mouseClicked(MouseEvent arg0) { // if the mouse is clicked
		vector1=geometry.convertMousetoCoord(arg0,SZ);
		Cursor defaultCursor= new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(defaultCursor);
		if (potentialClick==null && 
				(GeoPlayground.whatToDo==GeoPlayground.MakeInt ||
				 GeoPlayground.whatToDo==GeoPlayground.MeasureRatio ||
				 GeoPlayground.whatToDo==GeoPlayground.MeasureSum ||
				 GeoPlayground.whatToDo==GeoPlayground.MeasureDiff ||
				 GeoPlayground.whatToDo==GeoPlayground.MeasureProd)) clickedList.clear();
		if (geometry.mouseIsValid(vector1)) switch (GeoPlayground.whatToDo) {
		case GeoPlayground.TrackObject:
			getPotentialPointOrInt(vector1);
			trackPoint=potentialClick;
			potentialClick=null;
			break;
		case GeoPlayground.FixObject:
			getPotentialAny(vector1);
			fixedObject=potentialClick;
			potentialClick=null;
			break;
		case GeoPlayground.MakePoints:
		default:
			getPotentialComposite(vector1);
			if (clickedList.isEmpty()&&potentialClick!=null){
				clickedList.add(potentialClick);
				if(potentialClick.getType()==0)
					clickedList.add(geometry.createPoint(GeoConstruct.PTonCIRC,clickedList,vector1));
				else if(potentialClick.getType()<0)
					clickedList.add(geometry.createPoint(GeoConstruct.PTonLINE,clickedList,vector1));
			}
			else{
				clickedList.add(geometry.createPoint(GeoConstruct.POINT,clickedList,vector1));	
			}
			list.addLast(clickedList.getLast());
			updateNewStuff(list);
			clickedList.clear();
			break;
		case GeoPlayground.MakeMdPt:
			addToList(clickedList);
			if (clickedList.size()==2) {
				for (int i=0;i<2;i++)
					if (clickedList.get(i).getID()==-1) {
						list.addLast(clickedList.get(i));
						list.getLast().setID(list.size()-1);
						clickedList.get(i).setID(list.size()-1);
					}
				if (clickedList.get(0).getID()>clickedList.get(1).getID()) {
					GeoConstruct temp=clickedList.get(0);
					clickedList.set(0,potentialClick);
					clickedList.set(1,temp);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					// check to see if the midpoint already exists, if not, create it.
					if (list.get(k).getType()==MIDPT)
						if (list.get(k).constList.containsAll(clickedList))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createPoint(MIDPT,clickedList,vector1));
					updateNewStuff(list);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MakeFxPt:
			clickedList.clear();
			list.addLast(geometry.createPoint(FIXedPT,clickedList,vector1));
			updateNewStuff(list);
			break;
		case GeoPlayground.MeasureCnst:
			clickedList.clear();
			futureList.clear();
			repaint();
			list.addLast(geometry.createPoint(CONSTANT,clickedList,vector1));
			updateNewStuff(list);
			String measureValue = (String)JOptionPane.showInputDialog(
                    null,
                    GeoPlayground.cnstText+": ",
                    "",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
			if (measureValue!=null){
					if (measureValue.lastIndexOf('.')!=measureValue.indexOf('.'))
						measureValue=measureValue.substring(0,measureValue.lastIndexOf('.')-1);
					int n=measureValue.length();
					char[] crap=measureValue.toCharArray();
					String junk="";
					for (int i=0;i<n;i++)
						if ((crap[i]>='0' && crap[i]<='9') || (crap[i]=='-' && i==0) || crap[i]=='.')
							junk=junk+crap[i];
					measureValue=junk;
				}
			if (measureValue!=null && measureValue!="") {
				list.getLast().measureValue=Double.parseDouble(measureValue);
				list.getLast().measureValueNew=list.getLast().measureValue;
			}
			else list.removeLast();
			break;
		case GeoPlayground.MakeComment:
			clickedList.clear();
			futureList.clear();
			repaint();
			String labelTxt = (String)JOptionPane.showInputDialog(
                    null,
                    GeoPlayground.commentText+": ",null,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
			if (labelTxt==null) labelTxt="";
			{	char[] crap=labelTxt.toCharArray();						// we recreate
				int n=labelTxt.length();								// the string
				labelTxt="";											// after removing
				for (int i=0;i<n;i++)									// illegal
					if (crap[i]!='!' && crap[i]!=';' && crap[i]!='{' &&	// characters
						crap[i]!='}' && crap[i]!='<' && crap[i]!='>')	// so that SaveLoad
						labelTxt=labelTxt+crap[i];						// can still work.
			}
			list.addLast(geometry.createPoint(COMMENT,clickedList,vector1));
			list.getLast().setDisplayText(labelTxt);
			updateNewStuff(list);
			repaint();
			break;
		case GeoPlayground.MeasureCirc:
		  if (potentialClick!=null) {
		    boolean alreadyExists=false;
		    for (int k=0;k<list.size();k++) {
			  if (list.get(k).getType()==CIRCUMF && list.get(k).get(0)==potentialClick)
			    {alreadyExists=true;list.get(k).setShown(true);}
			}
			if (!alreadyExists) {
			  clickedList.addLast(potentialClick.get(0));
			  clickedList.addLast(potentialClick.get(1));
			  clickedList.addLast(potentialClick);
			  potentialClick.setLabelShown(true);
			  list.addLast(geometry.createPoint(CIRCUMF,clickedList,vector1));
			  updateNewStuff(list);
			}
			clickedList.clear();
		  }
		  break;
		case GeoPlayground.MeasureArea:
		  if (potentialClick!=null) {
		    boolean alreadyExists=false;
		    for (int k=0;k<list.size();k++) {
			  if (list.get(k).getType()==AREA && list.get(k).get(0)==potentialClick)
			    {alreadyExists=true;list.get(k).setShown(true);}
			}
			if (!alreadyExists) {
			  clickedList.addLast(potentialClick.get(0));
			  clickedList.addLast(potentialClick.get(1));
			  clickedList.addLast(potentialClick);
			  potentialClick.setLabelShown(true);
			  list.addLast(geometry.createPoint(AREA,clickedList,vector1));
			  updateNewStuff(list);
			}
			clickedList.clear();
		  }
		  break;
		case GeoPlayground.MeasureDist:
			addToList(clickedList);
			if (clickedList.size()==2) {
				for (int i=0;i<2;i++)
					if (clickedList.get(i).getID()==-1) {
						list.addLast(clickedList.get(i));
						list.getLast().setID(list.size()-1);
						clickedList.get(i).setID(list.size()-1);
					}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					// check to see if the distance already exists, if not, create it.
					if (list.get(k).getType()==DISTANCE)
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
				    list.addLast(geometry.createPoint(DISTANCE,clickedList,vector1));
				    updateNewStuff(list);
					list.getLast().get(0).setLabelShown(true);
					list.getLast().get(1).setLabelShown(true);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MeasureRatio:
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) 
			if (potentialClick!=clickedList.get(0)) {
				clickedList.add(potentialClick);
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					// check to see if the ratio already exists, if not, create it.
					if (list.get(k).getType()==RATIO)
						if (list.get(k).get(0).get(0)==clickedList.get(0) && list.get(k).get(0).get(1)==clickedList.get(1))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createPoint(RATIO,clickedList,vector1));
				  	list.getLast().setID(list.size()-1);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MeasureSum:
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) 
			if (potentialClick!=clickedList.get(0)) {
				clickedList.add(potentialClick);
				boolean alreadyExists=false;
				if (clickedList.get(0).getID()>clickedList.get(1).getID()) {
					GeoConstruct temp=clickedList.get(0);
					clickedList.set(0,potentialClick);
					clickedList.set(1,temp);
				}
				for (int k=0;k<list.size();k++) {
					// check to see if the sum already exists, if not, create it.
					if (list.get(k).getType()==SUM)
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createPoint(SUM,clickedList,vector1));
				  	list.getLast().setID(list.size()-1);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MeasureProd:
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) 
			if (potentialClick!=clickedList.get(0)) {
				clickedList.add(potentialClick);
				boolean alreadyExists=false;
				if (clickedList.get(0).getID()>clickedList.get(1).getID()) {
					GeoConstruct temp=clickedList.get(0);
					clickedList.set(0,potentialClick);
					clickedList.set(1,temp);
				}
				for (int k=0;k<list.size();k++) {
					// check to see if the sum already exists, if not, create it.
					if (list.get(k).getType()==PROD)
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createPoint(PROD,clickedList,vector1));
				  	list.getLast().setID(list.size()-1);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MeasureDiff:
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) 
			if (potentialClick!=clickedList.get(0)) {
				clickedList.add(potentialClick);
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					// check to see if the difference already exists, if not, create it.
					if (list.get(k).getType()==DIFF)
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createPoint(DIFF,clickedList,vector1));
				  	list.getLast().setID(list.size()-1);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MeasureAngle:
			addToList(clickedList);
			if (clickedList.size()==3) {
				for (int i=0;i<3;i++)
					if (clickedList.get(i).getID()==-1){
						list.add(clickedList.get(i));
						list.getLast().setID(list.size()-1);
					}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++)
					if (list.get(k).getType()==ANGLE)
					if (list.get(k).get(0)==clickedList.get(0))
					if (list.get(k).get(1)==clickedList.get(1))
					if (list.get(k).get(2)==clickedList.get(2))
							{alreadyExists=true;list.get(k).setShown(true);}
				if (!alreadyExists) {
				    list.add(geometry.createPoint(ANGLE,clickedList,vector1));
				    updateNewStuff(list);
					list.getLast().get(0).setLabelShown(true);
					list.getLast().get(1).setLabelShown(true);
					list.getLast().get(2).setLabelShown(true);
				}
				clickedList.clear();
			}
			
			break;
		case GeoPlayground.MakeBisect:
			addToList(clickedList);
			if (clickedList.size()==3) {
				for (int i=0;i<3;i++)
					if (clickedList.get(i).getID()==-1){
						list.add(clickedList.get(i));
						list.getLast().setID(list.size()-1);
					}
				if (clickedList.get(0).getID()>clickedList.get(2).getID()){
					GeoConstruct temp=clickedList.get(0);
					clickedList.set(0,clickedList.get(2));
					clickedList.set(2,temp);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++)
					if (list.get(k).getType()==BISECTOR)
					if (list.get(k).get(0)==clickedList.get(0) &&
						list.get(k).get(1)==clickedList.get(1) &&
						list.get(k).get(2)==clickedList.get(2))
							{alreadyExists=true;list.get(k).setShown(true);}
				if (!alreadyExists) {
					list.add(geometry.createLine(BISECTOR,clickedList));
					updateNewStuff(list);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MakeRays:
			addToList(clickedList);
			if (clickedList.size()==2) {
				for (int i=0;i<2;i++)
					if (clickedList.get(i).getID()==-1) {
						list.addLast(clickedList.get(i));
						list.getLast().setID(list.size()-1);
						clickedList.get(i).setID(list.size()-1);
					}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					// check to see if the ray already exists, if not, create it.
					if (list.get(k).getType()==RAY)
						if (list.get(k).constList.get(0)==clickedList.get(0) && list.get(k).constList.get(1)==clickedList.get(1))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createLine(RAY,clickedList));
					updateNewStuff(list);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MeasureTri:
			addToList(clickedList);
			if (clickedList.size()==3) {
				for (int i=0;i<3;i++)
					if (clickedList.get(i).getID()==-1){
						list.add(clickedList.get(i));
						list.getLast().setID(list.size()-1);
					}
				for (int i=0;i<2;i++) for (int j=i+1;j<3;j++)
				if (clickedList.get(i).getID()>clickedList.get(j).getID()){
					GeoConstruct temp=clickedList.get(i);
					clickedList.set(i,clickedList.get(j));
					clickedList.set(j,temp);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++)
					if (list.get(k).getType()==TRIANGLE)
					if (list.get(k).get(0)==clickedList.get(0) &&
						list.get(k).get(1)==clickedList.get(1) &&
						list.get(k).get(2)==clickedList.get(2))
							{alreadyExists=true;list.get(k).setShown(true);}
				if (!alreadyExists) {
					list.add(geometry.createPoint(TRIANGLE,clickedList,vector1));
					updateNewStuff(list);
					for (int i=0;i<3;i++) list.getLast().get(i).setLabelShown(true);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.MakeLines:
		case GeoPlayground.MakeSegment:
			addToList(clickedList);
			if (clickedList.size()==2) {
				for (int i=0;i<2;i++)
					if (clickedList.get(i).getID()==-1) {
						list.addLast(clickedList.get(i));
						list.getLast().setID(list.size()-1);
					}
				if (clickedList.get(0).getID()>clickedList.get(1).getID()) {
					GeoConstruct temp=clickedList.get(0);
					clickedList.set(0,clickedList.get(1));
					clickedList.set(1,temp);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					if ((list.get(k).getType()==LINE && GeoPlayground.whatToDo==GeoPlayground.MakeLines) ||
							(list.get(k).getType()==SEGMENT && GeoPlayground.whatToDo==GeoPlayground.MakeSegment))
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
							{alreadyExists=true; list.get(k).setShown(true);}
					if (list.get(k).getType()==LINE && GeoPlayground.whatToDo==GeoPlayground.MakeSegment)
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))  {
							alreadyExists=true;
							list.add(k,geometry.createLine(SEGMENT, clickedList));
							list.get(k).setID(k);
							list.get(k).setLabelShown(list.get(k+1).getLabelShown());
							list.get(k).displayText=list.get(k+1).displayText;
							for (int l=k+1;l<list.size();l++) {				//
								for (int m=0;m<list.get(l).getSize();m++) {	//
									if (list.get(l).get(m)==list.get(k+1))	//
										list.get(l).set(m,list.get(k));		//
								}											//
							}												//
							if (fixedObject==list.get(k+1)) fixedObject=list.get(k);
							list.remove(k+1);
							{
								LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();
								for (int l=k+1;l<list.size();l++)
									if ((list.get(l).getType()==PTonLINE && list.get(l).get(0)==list.get(k)) ||
										((list.get(l).getType()>=LINEintLINE0 && list.get(l).getType()<=CIRCintLINE1 &&
										(list.get(l).get(0)==list.get(k) || list.get(l).get(1)==list.get(k)))))
											tempList.add(list.get(k));
								if (tempList.size()>0) {
									for (int l=tempList.get(0).getID();l<list.size();l++)
										for (int m=0;m<tempList.get(0).getSize();m++)
											if (tempList.contains(list.get(l).get(m)))
												tempList.add(list.get(l));
									for (int l=0;l<tempList.size();l++) {
										tempList.get(l).update();
										tempList.get(l).getNewXYZ(vector1);
										tempList.get(l).setXYZ(vector1);
										tempList.get(l).setValid(tempList.get(l).getValidNew());
									}
								}
							}
						}
					if (list.get(k).getType()==SEGMENT && GeoPlayground.whatToDo==GeoPlayground.MakeLines)
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1)) {
							alreadyExists=true;
							list.add(k,geometry.createLine(LINE, clickedList));
							list.get(k).setID(k);
							list.get(k).setLabelShown(list.get(k+1).getLabelShown());
							list.get(k).displayText=list.get(k+1).displayText;
							for (int l=k+1;l<list.size();l++) {				//
								for (int m=0;m<list.get(l).getSize();m++) {	//
									if (list.get(l).get(m)==list.get(k+1))	//
										list.get(l).set(m,list.get(k));		//
								}											//
							}												//
							if (fixedObject==list.get(k+1)) fixedObject=list.get(k);
							list.remove(k+1);
							LinkedList<GeoConstruct> tempList=new LinkedList<GeoConstruct>();
							for (int l=k+1;l<list.size();l++)
								if ((list.get(l).getType()==PTonLINE && list.get(l).get(0)==list.get(k)) ||
									((list.get(l).getType()>=LINEintLINE0 && list.get(l).getType()<=CIRCintLINE1 &&
									(list.get(l).get(0)==list.get(k) || list.get(l).get(1)==list.get(k)))))
										tempList.add(list.get(k));
							if (tempList.size()>0) {
								for (int l=tempList.get(0).getID();l<list.size();l++)
									for (int m=0;m<tempList.get(0).getSize();m++)
										if (tempList.contains(list.get(l).get(m)))
											tempList.add(list.get(l));
								for (int l=0;l<tempList.size();l++) {
									tempList.get(l).update();
									tempList.get(l).getNewXYZ(vector1);
									tempList.get(l).setXYZ(vector1);
									tempList.get(l).setValid(tempList.get(l).getValidNew());
								}
							}
					}
				}
				if (!alreadyExists && clickedList.get(1)!=clickedList.get(0)) {
					if (GeoPlayground.whatToDo==GeoPlayground.MakeLines)
						list.addLast(geometry.createLine(LINE,clickedList));
					else
						list.addLast(geometry.createLine(SEGMENT,clickedList));
					list.getLast().setID(list.size()-1);
					list.getLast().update();
				}
				clickedList.clear();
				break;
			}
			break;
		case GeoPlayground.MakeCircles:
			addToList(clickedList);
			if(clickedList.size()==2) {
				for (int i=0;i<2;i++)
					if (clickedList.get(i).getID()==-1) {
						list.addLast(clickedList.get(i));
						list.getLast().setID(list.size()-1);
					}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					if (list.get(k).getType()==CIRCLE)
						if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
						{alreadyExists=true; list.get(k).setShown(true);}
				}
				if (!alreadyExists && clickedList.get(0)!=clickedList.get(1)) {
					list.addLast(geometry.createCircle(CIRCLE,clickedList)); 
					list.getLast().setID(list.size()-1);
				}
				clickedList.clear();
				break;
			}
			break;
		case GeoPlayground.ReflectPt:
			if (!clickedList.isEmpty()){
				addToList(clickedList);
				potentialClick=clickedList.getLast();
			}
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) {
				if (clickedList.get(1).getID()==-1) {
					list.addLast(clickedList.getLast());
					list.getLast().setID(list.size()-1);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					if (list.get(k).getType()==REFLECT_PT)
					if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
					{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createPoint(REFLECT_PT,clickedList,vector1));
					updateNewStuff(list);
				}
				clickedList.clear();
				break;
			}
			break;
		case GeoPlayground.RotatePt:
			if (clickedList.size()==0) {
				if (potentialClick!=null)
					clickedList.add(potentialClick);
			}
			else {
				addToList(clickedList);
				if (clickedList.get(1).getID()==-1) {
					list.add(clickedList.get(1));
					list.getLast().setID(list.size()-1);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++)
					if (list.get(k).getType()==ROTATE_PT)
					if (list.get(k).get(0)==clickedList.get(0) &&
						list.get(k).get(1)==clickedList.get(1))
							{alreadyExists=true;list.get(k).setShown(true);}
				if (!alreadyExists) {
					list.add(geometry.createPoint(ROTATE_PT,clickedList,vector1));
					updateNewStuff(list);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.TranslatePt:
			if (clickedList.size()==0) {
				if (potentialClick!=null)
					clickedList.add(potentialClick);
			}
			else {
				addToList(clickedList);
				if (clickedList.get(1).getID()==-1) {
					list.add(clickedList.get(1));
					list.getLast().setID(list.size()-1);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++)
					if (list.get(k).getType()==TRANSLATE_PT)
					if (list.get(k).get(0)==clickedList.get(0) &&
						list.get(k).get(1)==clickedList.get(1))
							{alreadyExists=true;list.get(k).setShown(true);}
				if (!alreadyExists) {
					list.add(geometry.createPoint(TRANSLATE_PT,clickedList,vector1));
					updateNewStuff(list);
				}
				clickedList.clear();
			}
			break;
		case GeoPlayground.InvertPt:
			if (!clickedList.isEmpty()){
				addToList(clickedList);
				potentialClick=clickedList.getLast();
			}
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) {
				if (clickedList.get(1).getID()==-1) {
					list.addLast(clickedList.getLast());
					list.getLast().setID(list.size()-1);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					if (list.get(k).getType()==INVERT_PT)
					if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
					{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createPoint(INVERT_PT,clickedList,vector1));
					updateNewStuff(list);
				}
				clickedList.clear();
				break;
			}
			break;
		case GeoPlayground.MakePerps:
			if (!clickedList.isEmpty()){
				if (potentialClick==null)
					if (clickedList.get(0).mouseIsOver(vector1,SZ))
						potentialClick=clickedList.get(0);
				addToList(clickedList);
				potentialClick=clickedList.getLast();
			}
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) {
				if (clickedList.get(1).getID()==-1) {
					list.addLast(clickedList.getLast());
					list.getLast().setID(list.size()-1);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					if (list.get(k).getType()==PERP)
					if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
					{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createLine(PERP,clickedList));
					updateNewStuff(list);
				}
				clickedList.clear();
				break;
			}
			break;
		case GeoPlayground.MakeParlls:
			if (!clickedList.isEmpty()){
				addToList(clickedList);
				potentialClick=clickedList.getLast();
			}
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) {
				if (clickedList.get(1).getID()==-1) {
					list.addLast(clickedList.getLast());
					list.getLast().setID(list.size()-1);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					if (list.get(k).getType()==PARLL0 || list.get(k).getType()==PARLL1)
					if (list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1))
					{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					list.addLast(geometry.createLine(PARLL0,clickedList));
					updateNewStuff(list);
					if (geometry.getGeometry()==3) {
						list.addLast(geometry.createLine(PARLL1,clickedList));
						updateNewStuff(list);
					}
				}
				clickedList.clear();
				break;
			}
			break;
		case GeoPlayground.MakeInt:
			if (clickedList.isEmpty() && potentialClick!=null) clickedList.add(potentialClick);
			else if (potentialClick!=null) {
				if (clickedList.get(0).getID()>potentialClick.getID()) {
					clickedList.addFirst(potentialClick);
				}
				else{
					clickedList.add(potentialClick);
				}
				boolean alreadyExists=false;
				for (int k=0;k<list.size();k++) {
					if (list.get(k).getType()>=LINEintLINE0 && list.get(k).getType()<30)
						if ((list.get(k).get(0)==clickedList.get(0) && list.get(k).get(1)==clickedList.get(1)))
						{alreadyExists=true;list.get(k).setShown(true);}
				}
				if (!alreadyExists) {
					geometry.createIntersections(clickedList, list);
				}
				clickedList.clear();
				break;
			}
			break;
		case GeoPlayground.HideObject:
			if (potentialClick!=null) {
				potentialClick.setShown(false);
				if (potentialClick==fixedObject) fixedObject=null;
				if (potentialClick==trackPoint) trackPoint=null;
			}
			break;
		case GeoPlayground.LabelObject:
			if (potentialClick!=null) {
				if (arg0.getButton()==arg0.BUTTON1 && !arg0.isControlDown()) potentialClick.setLabelShown(!potentialClick.getLabelShown());
				else {// if rightClicked, then create Popup to relabel.
					GeoConstruct temp=potentialClick;
					String labelText = (String)JOptionPane.showInputDialog(
					                    null,
					                    GeoPlayground.newLabelText,
					                    GeoPlayground.currentLabelText+"\""+temp.displayText+"\"",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    null,
					                    null);
					if (labelText==null) labelText="";
					{	char[] crap=labelText.toCharArray();					// we recreate
						int n=labelText.length();								// the string
						labelText="";											// after removing
						for (int i=0;i<n;i++)									// illegal
							if (crap[i]!='!' && crap[i]!=';' && crap[i]!='{' &&	// characters
								crap[i]!='}' && crap[i]!='<' && crap[i]!='>')	// so that SaveLoad
								labelText=labelText+crap[i];					// can still work.
					}															//
					if (labelText!=null)
					if (labelText.length()>0 && !labelText.equals(temp.displayText)) {
						LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
						tempList.add(temp);
						for (int i=temp.getID()+1;i<list.size();i++) {
							for(int j=0;j<tempList.size();j++)
								if(list.get(i).constList.contains(tempList.get(j)))
									{tempList.add(list.get(i));break;}
						}
						String oldLabel=temp.displayText;
						temp.setDisplayText(labelText);
						temp.setLabelShown(true);
						for (int i=1;i<tempList.size();i++)
							if (tempList.get(i).displayText.contains(oldLabel))
								tempList.get(i).setDisplayText();
						repaint();
					}
				}
			}
			break;
		case GeoPlayground.Move:
			break;
		}
		potentialClick=null;
		lastPotential=null;
		futureList.clear();
		repaint();
	}
}