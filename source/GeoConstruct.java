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
 
import java.awt.Graphics;
import java.util.LinkedList;

abstract class GeoConstruct {
  public static final int BISECTOR=-4,PARLL1=-5,PARLL0=-6,	// types of lines
						  SEGMENT=-3,PERP=-2,LINE=-1,		// types of lines
						  RAY=-7,							// types of lines
						  CIRCLE=0,						    // circle
						  POINT=1,PTonLINE=2,PTonCIRC=3,    // points that can be moved
						  LINEintLINE0=4,LINEintLINE1=5,    // points that can't be moved
						  CIRCintLINE0=6,CIRCintLINE1=7,    // points that can't be moved
						  CIRCintCIRC00=8,CIRCintCIRC01=9,  // points that can't be moved
						  CIRCintCIRC10=10,CIRCintCIRC11=11,// points that can't be moved
						  MIDPT=12,FIXedPT=13,			    // points that can't be moved
						  REFLECT_PT=14,ROTATE_PT=15,		// points that can't be moved
						  TRANSLATE_PT=16,INVERT_PT=17,		// points that can't be moved
						  DISTANCE=32,ANGLE=33,				// measures
						  CIRCUMF=34,AREA=35,				// measures
						  SUM=36,RATIO=37,DIFF=38,PROD=39,	// measures
						  TRIANGLE=40,						// measures
  						  CONSTANT=50,						// measures
  						  COMMENT=60;
  // note that all lines have negative indices and all points have positive indices.
  protected double x=0,y=0,z=0,newX=0,newY=0,newZ=0;
  protected LinkedList<GeoConstruct> constList =new LinkedList<GeoConstruct>();
  protected int type;
  protected int ID=-1;
  protected boolean shown=true, labelShown=false, isReal=true, isRealNew=true;
  String displayText="";
  double measureValue=0, measureValueNew=0;
  
  protected GeoConstruct(){}
  public GeoConstruct(int t, double[] vector)
  { type=t; setXYZ(vector);}
  public GeoConstruct(int t, double[] v1, double[] v2)
  { type=t; setXYZ(v1, v2);}
  public GeoConstruct(int t, GeoConstruct a, GeoConstruct b){
    double[] vec1={0,0,0}, vec2={0,0,0};
    type=t;
    constList.addLast(a);
    constList.addLast(b);
    constList.get(0).getXYZ(vec1);	constList.get(1).getXYZ(vec2);
    this.setXYZ(vec1, vec2);    
  }
  public GeoConstruct(int t, double[] v, GeoConstruct a) {
    type=t;
    constList.addLast(a);
    this.setXYZ(v);
  }

  public int getType() { return type; }
  public int getID() { return ID;}
  public boolean getShown() { return shown; } 
  public boolean getLabelShown() { return labelShown; }
  public double getX() { return x; }  public double getNewX() { return newX; } 
  public double getY() { return y; }  public double getNewY() { return newY; } 
  public double getZ() { return z; }  public double getNewZ() { return newZ; } 
  public boolean getValid() { return isReal; }
  public boolean getValidNew() { return isRealNew; }
  public int getSize() {return constList.size();}
  public GeoConstruct get(int i){if(constList.size()>i) return constList.get(i); else return null;}
  public void set(int m, GeoConstruct a) {
	  if (a!=null) {
		  constList.remove(m);
		  constList.add(m,a);
	  }
  }
  public void getXYZ(double[] vector) {vector[0]=x; vector[1]=y; vector[2]=z;}
  public void getNewXYZ(double[] vector) {vector[0]=newX; vector[1]=newY; vector[2]=newZ;}
  public void setID(int i){
	  if (displayText.equals(""+(char)('A'+ID%26)+""+(ID/26)) || displayText.equals("")) {
		  ID=i;
		  displayText = ""+(char)('A'+ID%26)+""+(ID/26);
	  }
	  else ID=i;
  }
  public void setType(int t) {type=t;}
  public void setShown(boolean b) { shown=b; }
  public void setValid(boolean x) { isReal=x; isRealNew=x;}
  public void setValidNew(boolean x) { isRealNew=x; }
  public void setLabelShown(boolean b) { labelShown=b; }
  public abstract void setXYZ(double[] vector);
  public abstract void setXYZ(double[] v1, double[] v2);
  public abstract void setNewXYZ(double[] vector);
  public abstract void setNewXYZ(double[] v1, double[] v2);
  public abstract void draw(Graphics g, int SZ, boolean New);
  public abstract boolean mouseIsOver(double[] v1,int SZ);
  public abstract void update();  
  public abstract void translate(double[] dragStart, double[] dragNow);
  public abstract void transform(GeoConstruct fixedObject,double[] dragStart,double[] dragNow);
  public static double getScale(){return 0;}
  public static void setScale(double s){}
  public static void resetScale(){}
  public void setDisplayText(String s) {displayText=s;}
  public void setDisplayText() {
	  if (type>30 && (displayText.equals("") || displayText.contains("[") || displayText.contains("("))) {
		  if (type==DISTANCE) 	displayText="d("+get(0).displayText+","+get(1).displayText+")";
		  if (type==ANGLE)		displayText="\u03b8("+get(0).displayText+","+get(1).displayText+","+get(2).displayText+")";
		  if (type==TRIANGLE)	displayText="A(\u0394["+get(0).displayText+","+get(1).displayText+","+get(2).displayText+"])";
		  if (type==CIRCUMF)	displayText="c("+get(2).displayText+")";
		  if (type==AREA)		displayText="A("+get(2).displayText+")";
		  if (type==SUM)		displayText="["+get(0).displayText+"+"+get(1).displayText+"]";
		  if (type==DIFF)		displayText="["+get(0).displayText+"-"+get(1).displayText+"]";
		  if (type==PROD)		displayText="["+get(0).displayText+"\u00b7"+get(1).displayText+"]";
		  if (type==RATIO)		displayText="["+get(0).displayText+":"+get(1).displayText+"]";
	  }
  }
  public String getDisplayText() {return displayText;}

} // end class 

