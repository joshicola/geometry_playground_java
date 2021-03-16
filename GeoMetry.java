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
 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.filechooser.FileFilter;
import jpsxdec.FileNameExtensionFilter;

public abstract class GeoMetry {
	public final int BISECTOR=GeoConstruct.BISECTOR,
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
// note that all lines have negative indices and all points have positive indices.
  public abstract int getGeometry();
  public abstract void drawModel(Graphics g, int SZ);
  public abstract double[] convertMousetoCoord(MouseEvent arg0,int SZ);
  public boolean mouseIsValid(double[] vector1)
    {return (1 < vector1[2]);}
  public abstract GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1);
  public abstract GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList);
  public abstract GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList);
  public abstract void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list);
  public double getScale() {return 1;}
  public void setScale(double s){}

  public abstract String extension();
  public abstract String getName();
  public abstract FileFilter getFileFilter();
}

/************************************************************************************************************************/
class SphericalGeometry extends GeoMetry{
  public int getGeometry() {return 0;}
  public void drawModel(Graphics g,int SZ){
	for (int i=0;i<4;i++) {
	  GeoPlayground.modelMI[i].setText(GeoPlayground.modelText[0][i]);  
	  GeoPlayground.modelMI[i].setEnabled(i<3);
	  GeoPlayground.modelMI[i].setVisible(i<3);
	}
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	switch (GeoPlayground.model) {
	  case 0:
	    SphericalConstruct.resetScale();
        g.setColor(Color.black);        	// first we draw the
        g.drawOval(0+fudge,0,SZ*2,SZ*2);    // playing field
	    break;
	  case 1:
	    break;
	  case 2:
	  case 3:
		SphericalConstruct.resetScale();
		g.setColor(Color.black);
		g.drawLine(fudge,0,fudge,2*SZ);
		g.drawLine(2*SZ+fudge,0,2*SZ+fudge,2*SZ);
		break;
	}
  }
  
  public double[] convertMousetoCoord(MouseEvent arg0,int SZ){
    double[] v={0,0,0};
    int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	v[0]=(double)(arg0.getX()-SZ-fudge)/SZ;
    v[1]=(double)(arg0.getY()-SZ)/SZ;
    v[2]=Math.sqrt(1-v[0]*v[0]-v[1]*v[1]);
	switch (GeoPlayground.model) {
	  case 0:
	    break;
	  case 1:
		  SphericalConstruct.unscale(v);
	  double d=1/(1+v[0]*v[0]+v[1]*v[1]);
	    v[2]=(1-v[0]*v[0]-v[1]*v[1])*d;
		v[0]*=(2*d);
		v[1]*=(2*d);
	    break;
	  case 2:
	  case 3:
	    double phi=Math.PI-2*Math.atan(Math.exp(Math.PI*v[1]));
		double theta=-Math.PI*v[0];
		v[0]=Math.cos(theta)*Math.sin(phi);
		v[2]=Math.sin(theta)*Math.sin(phi);
		v[1]=Math.cos(phi);
	    break;
	}
	return v;
  }
  
  public boolean mouseIsValid(double[] vector1){
    if (GeoPlayground.model==0 && (vector1[0]*vector1[0]+vector1[1]*vector1[1]>1))
	  return false;
    return true;
  }
  
  public GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1){
		switch(Type){
		  case POINT: return new SphericalPoint(Type,clickedList,vector1);
		  case PTonLINE: return new SphericalPTonLINE(Type,clickedList,vector1);
		  case PTonCIRC: return new SphericalPTonCIRC(Type,clickedList,vector1);
		  case MIDPT:return new SphericalMIDPT(Type,clickedList,vector1);
		  case FIXedPT: return new SphericalFIXedPT(Type,clickedList,vector1);
		  case REFLECT_PT: return new SphericalReflectPt(Type,clickedList,vector1);
		  case ROTATE_PT: return new SphericalRotatePt(Type,clickedList,vector1);
		  case TRANSLATE_PT: return new SphericalTranslatePt(Type,clickedList,vector1);
		  case INVERT_PT: return new SphericalInvertPt(Type,clickedList,vector1);
		  case DISTANCE: return new SphericalDISTANCE(Type,clickedList,vector1);
		  case ANGLE: return new SphericalANGLE(Type,clickedList,vector1);
		  case CIRCUMF: return new SphericalCIRCUMF(Type,clickedList,vector1);
		  case AREA: return new SphericalAREA(Type,clickedList,vector1);
		  case RATIO: return new SphericalRATIO(Type,clickedList,vector1);
		  case SUM: 
		  case DIFF:
		  case PROD: return new SphericalSUM(Type,clickedList,vector1);
		  case COMMENT: return new SphericalCOMMENT(Type,clickedList,vector1);
		  case TRIANGLE: return new SphericalTRIANGLE(Type,clickedList,vector1);
		  case CONSTANT: return new SphericalCONSTANT(Type,clickedList,vector1);
		  case LINEintLINE0:
		  case LINEintLINE1:return new SphericalLINEintLINE(Type,clickedList,vector1);
		  case CIRCintLINE0:
		  case CIRCintLINE1:return new SphericalCIRCintLINE(Type,clickedList,vector1);
		  case CIRCintCIRC00:
		  case CIRCintCIRC01:
		  case CIRCintCIRC10:
		  case CIRCintCIRC11:return new SphericalCIRCintCIRC(Type,clickedList,vector1);
		  default: 
		    return null;
		}
  }

  public GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList){
		 switch(Type){
		   case RAY: Type=LINE;
		   case LINE: return new SphericalLine(Type,clickedList);
		   case PERP: return new SphericalPERP(Type,clickedList);
		   case SEGMENT: return new SphericalSEGMENT(Type,clickedList);
		   case BISECTOR: return new SphericalBISECTOR(Type,clickedList);
		   default:
			  	return null;
		 }
  }

  public GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList){
    return new SphericalCircle(Type,clickedList);
  }
  
  public void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list){
    for(int m=0;m<2;m++){
      SphericalPoint newPoint;
      newPoint=((SphericalConstruct)clickedList.get(0)).intersect(m,(SphericalConstruct)clickedList.get(1));
      list.addLast(newPoint);
      list.getLast().setID(list.size()-1);
      list.getLast().update();
      double[] v={0,0,0};
      list.getLast().getNewXYZ(v);	list.getLast().setXYZ(v);
      list.getLast().setValid(list.getLast().getValidNew());
    }
  }

  public double getScale(){return SphericalConstruct.getScale();}
  public void setScale(double s){SphericalConstruct.setScale(s);}
  
  public String extension(){return "sph";}  
  
  public String getName(){return "Spherical";}
  
  public FileFilter getFileFilter(){
    return new FileNameExtensionFilter("Spherical file", "sph");
  }

}

/************************************************************************************************************************/
class ProjectiveGeometry extends GeoMetry{
  public int getGeometry() {return 1;}
  public void drawModel(Graphics g,int SZ){
	for (int i=0;i<4;i++) {
	  GeoPlayground.modelMI[i].setText(GeoPlayground.modelText[1][i]);  
	  GeoPlayground.modelMI[i].setEnabled(i<2);
	  GeoPlayground.modelMI[i].setVisible(i<2);
	}
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    switch (GeoPlayground.model) {
	  case 0:
		ProjectiveConstruct.resetScale();
        g.setColor(Color.black);        // first we draw the
        g.drawOval(0+fudge,0,SZ*2,SZ*2);      // playing field
	    break;
	  case 1:
	  case 2:
	  case 3:
	    break;
	}
  }
  
  public double[] convertMousetoCoord(MouseEvent arg0,int SZ){
    double[] v={0,0,0};
    int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	v[0]=(double)(arg0.getX()-SZ-fudge)/SZ;
	v[1]=(double)(arg0.getY()-SZ)/SZ;
	v[2]=Math.sqrt(1-v[0]*v[0]-v[1]*v[1]);   
    switch (GeoPlayground.model) {
	  case 0:
	    break;
	  case 1:
	  case 2:
	  case 3:
		  ProjectiveConstruct.unscale(v);
	    v[2]=1/Math.sqrt(v[0]*v[0]+v[1]*v[1]+1);
		v[0]*=v[2];
		v[1]*=v[2];
	    break;
	}
    //ProjectiveConstruct.unscale(v);
	return v;
  }
  
  public boolean mouseIsValid(double[] vector1){
    if (GeoPlayground.model==0 && (vector1[0]*vector1[0]+vector1[1]*vector1[1]>1))
	  return false;
	return true;
  }

  public GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1){
		switch(Type){
		  case POINT: return new ProjectivePoint(Type,clickedList,vector1);
		  case PTonLINE: return new ProjectivePTonLINE(Type,clickedList,vector1);
		  case PTonCIRC: return new ProjectivePTonCIRC(Type,clickedList,vector1);
		  case MIDPT:return new ProjectiveMIDPT(Type,clickedList,vector1);
		  case FIXedPT: return new ProjectiveFIXedPT(Type,clickedList,vector1);
		  case REFLECT_PT: return new ProjectiveReflectPt(Type,clickedList,vector1);
		  case ROTATE_PT: return new ProjectiveRotatePt(Type,clickedList,vector1);
		  case TRANSLATE_PT: return new ProjectiveTranslatePt(Type,clickedList,vector1);
		  case INVERT_PT: return new ProjectiveInvertPt(Type,clickedList,vector1);
		  case DISTANCE: return new ProjectiveDISTANCE(Type,clickedList,vector1);
		  case ANGLE: return new ProjectiveANGLE(Type,clickedList,vector1);
		  case CIRCUMF: return new ProjectiveCIRCUMF(Type,clickedList,vector1);
		  case AREA: return new ProjectiveAREA(Type,clickedList,vector1);		  
		  case RATIO: return new ProjectiveRATIO(Type,clickedList,vector1);		  
		  case SUM:		  
		  case DIFF:
		  case PROD: return new ProjectiveSUM(Type,clickedList,vector1);
		  case COMMENT: return new ProjectiveCOMMENT(Type,clickedList,vector1);
		  case TRIANGLE: return new ProjectiveTRIANGLE(Type,clickedList,vector1);
		  case CONSTANT: return new ProjectiveCONSTANT(Type,clickedList,vector1);
		  case LINEintLINE0:
		  case LINEintLINE1:return new ProjectiveLINEintLINE(Type,clickedList,vector1);
		  case CIRCintLINE0:
		  case CIRCintLINE1:return new ProjectiveCIRCintLINE(Type,clickedList,vector1);
		  case CIRCintCIRC00:
		  case CIRCintCIRC01:
		  case CIRCintCIRC10:
		  case CIRCintCIRC11:return new ProjectiveCIRCintCIRC(Type,clickedList,vector1);		  
		  default: 
		    return null;
		}
  }

  public GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList){
		 switch(Type){
		   case RAY: Type=LINE;
		   case LINE: return new ProjectiveLine(Type,clickedList);
		   case PERP: return new ProjectivePERP(Type,clickedList);
		   case SEGMENT: return new ProjectiveSEGMENT(Type,clickedList);
		   case BISECTOR: return new ProjectiveBISECTOR(Type,clickedList);
		   default:
			  	return null;
		 }
  }

  public GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList){
    return new ProjectiveCircle(Type,clickedList);
  }
  
  public void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list){
    for(int m=0;m<4;m++){
      ProjectivePoint newPoint;
      newPoint=((ProjectiveConstruct)clickedList.get(0)).intersect(m,(ProjectiveConstruct)clickedList.get(1));
      list.addLast(newPoint);
      list.getLast().setID(list.size()-1);
      list.getLast().update();
      double[] v={0,0,0};
      list.getLast().getNewXYZ(v);	list.getLast().setXYZ(v);
      list.getLast().setValid(list.getLast().getValidNew());
      if(clickedList.get(1).getType()!=GeoConstruct.CIRCLE&&clickedList.get(0).getType()!=GeoConstruct.CIRCLE) break;
      if(m>=1&&(clickedList.get(1).getType()!=GeoConstruct.CIRCLE||clickedList.get(0).getType()!=GeoConstruct.CIRCLE)) break;
    }
  }

  public double getScale(){return ProjectiveConstruct.getScale();}
  public void setScale(double s){ProjectiveConstruct.setScale(s);}
  
  public String extension(){return "prj";}
  
  public String getName(){return "Projective";}
  
  public FileFilter getFileFilter(){
    return new FileNameExtensionFilter("Projective file", "prj");
  }
  
}

/************************************************************************************************************************/
class EuclideanGeometry extends GeoMetry{
  public int getGeometry() {return 2;}
  public void drawModel(Graphics g,int SZ){
	for (int i=0;i<4;i++) {
	  GeoPlayground.modelMI[i].setText(GeoPlayground.modelText[2][i]);  
	  GeoPlayground.modelMI[i].setEnabled(i<3);
	  GeoPlayground.modelMI[i].setVisible(i<3);
	}
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	switch (GeoPlayground.model) {
	  case 0:
        break;
	  case 1:
	    EuclideanConstruct.resetScale();
		g.setColor(Color.black);        // first we draw the
        g.drawOval(0+fudge,0,SZ*2,SZ*2);      // playing field
	    break;
	  case 2:
	  case 3:
	    EuclideanConstruct.resetScale();
	    break;
	}
  }
  public double[] convertMousetoCoord(MouseEvent arg0,int SZ){
    double[] vector1={0,0,0};
    int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	vector1[0]=(double)(arg0.getX()-SZ-fudge)/SZ;
    vector1[1]=(double)(arg0.getY()-SZ)/SZ;
    vector1[2]=0.;
    switch (GeoPlayground.model){
	  case 0:
	    break;
	  case 1:
	    if (vector1[0]*vector1[0]+vector1[1]*vector1[1]<1) {
		  double temp=Math.sqrt(1-vector1[0]*vector1[0]-vector1[1]*vector1[1]);
		  if (temp==0) temp=.00000001;
		  vector1[0]/=temp;
		  vector1[1]/=temp;
		  if (vector1[0]*vector1[0]+vector1[1]*vector1[1]==0) vector1[0]=.00000001;
		}
		else {
		  vector1[0]=0.;
		  vector1[1]=0.;
		}
	    break;
	  case 2:
	  case 3:
	    vector1[2]=vector1[0]*vector1[0]+vector1[1]*vector1[1];
	    if (vector1[2]==0) {vector1[0]=.00000001;vector1[2]=.0000000000000001;}
		vector1[0]/=vector1[2];
		vector1[1]/=vector1[2];
		vector1[2]=0;
		break;
	}
	EuclideanConstruct.unscale(vector1);
    return vector1;
  }
  
  public boolean mouseIsValid(double[] vector1){
    if (GeoPlayground.model==1 && vector1[0]==0. && vector1[1]==0.) return false;
	else return true;
  }

   public GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1){
	switch(Type){
	  case POINT: return new EuclideanPoint(Type,clickedList,vector1);
	  case PTonLINE: return new EuclideanPTonLINE(Type,clickedList,vector1);
	  case PTonCIRC: return new EuclideanPTonCIRC(Type,clickedList,vector1);
	  case MIDPT:return new EuclideanMIDPT(Type,clickedList,vector1);
	  case FIXedPT: return new EuclideanFIXedPT(Type,clickedList,vector1);
	  case REFLECT_PT: return new EuclideanReflectPt(Type,clickedList,vector1);
	  case TRANSLATE_PT: return new EuclideanTranslatePt(Type,clickedList,vector1);
	  case INVERT_PT: return new EuclideanInvertPt(Type,clickedList,vector1);
	  case ROTATE_PT: return new EuclideanRotatePt(Type,clickedList,vector1);
	  case DISTANCE: return new EuclideanDISTANCE(Type,clickedList,vector1);
	  case ANGLE: return new EuclideanANGLE(Type,clickedList,vector1);
	  case CIRCUMF: return new EuclideanCIRCUMF(Type,clickedList,vector1);
	  case AREA: return new EuclideanAREA(Type,clickedList,vector1);		  
	  case RATIO: return new EuclideanRATIO(Type,clickedList,vector1);		  
	  case SUM:  
	  case DIFF:
	  case PROD: return new EuclideanSUM(Type,clickedList,vector1);
	  case COMMENT: return new EuclideanCOMMENT(Type,clickedList,vector1);
	  case TRIANGLE: return new EuclideanTRIANGLE(Type,clickedList,vector1);
	  case CONSTANT: return new EuclideanCONSTANT(Type,clickedList,vector1);
	  case LINEintLINE0:
	  case LINEintLINE1:return new EuclideanLINEintLINE(Type,clickedList,vector1);
	  case CIRCintLINE0:
	  case CIRCintLINE1:return new EuclideanCIRCintLINE(Type,clickedList,vector1);
	  case CIRCintCIRC00:
	  case CIRCintCIRC01:
	  case CIRCintCIRC10:
	  case CIRCintCIRC11:return new EuclideanCIRCintCIRC(Type,clickedList,vector1);
	  default: 
	    return null;
	}
  }
  public GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList){
	 switch(Type){
	   case LINE: return new EuclideanLine(Type,clickedList);
	   case PERP: return new EuclideanPERP(Type,clickedList);
	   case SEGMENT: return new EuclideanSEGMENT(Type,clickedList);
	   case BISECTOR: return new EuclideanBISECTOR(Type,clickedList);
	   case PARLL0: return new EuclideanPARLL0(Type,clickedList);
	   case RAY: return new EuclideanLine(Type,clickedList);
	   default:
		  	return null;
	 }
  }
  public GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList){
    return new EuclideanCircle(Type,clickedList);
  }
  public void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list){
    for(int m=0;m<2;m++){
      EuclideanPoint newPoint;
      newPoint=((EuclideanConstruct)clickedList.get(0)).intersect(m,(EuclideanConstruct)clickedList.get(1));
      list.addLast(newPoint);
      list.getLast().setID(list.size()-1);
      list.getLast().update();
      double[] v={0,0,0};
      list.getLast().getNewXYZ(v);	list.getLast().setXYZ(v);
      list.getLast().setValid(list.getLast().getValidNew());
      if(clickedList.get(1).getType()!=GeoConstruct.CIRCLE&&clickedList.get(0).getType()!=GeoConstruct.CIRCLE) break;
    }
  }
  public double getScale(){return EuclideanConstruct.getScale();}
  public void setScale(double s){EuclideanConstruct.setScale(s);}
  
  public String extension(){return "euc";}
  
  public String getName(){return "Euclidean";}
  
  public FileFilter getFileFilter(){
    return new FileNameExtensionFilter("Euclidean file", "euc");
  }
}

/************************************************************************************************************************/
class ToroidalGeometry extends GeoMetry{
  public int getGeometry() {return 5;}
  public void drawModel(Graphics g,int SZ){
	for (int i=0;i<4;i++) {
	  GeoPlayground.modelMI[i].setText(GeoPlayground.modelText[5][i]);  
	  GeoPlayground.modelMI[i].setEnabled(i<3);
	  GeoPlayground.modelMI[i].setVisible(i<3);
	}
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	switch (GeoPlayground.model) {
	  case 0:
		g.setColor(new Color(128,128,0));
		g.drawLine(fudge,0,fudge,2*SZ);		g.drawLine(2*SZ+fudge,0,2*SZ+fudge,2*SZ);
		g.setColor(new Color(128,0,128));
		g.drawLine(fudge,0,2*SZ+fudge,0);	g.drawLine(fudge,2*SZ,2*SZ+fudge,2*SZ);
        break;
	  case 1:
		g.setColor(new Color(128,128,0));
		for (int i=0;i<3;i++) g.drawLine(fudge+GeoPlayground.getHght()/3*i,0,fudge+GeoPlayground.getHght()/3*i,2*SZ);
		g.drawLine(fudge+2*SZ,0,fudge+2*SZ,2*SZ);
		g.setColor(new Color(128,0,128));
		for (int i=0;i<3;i++) g.drawLine(fudge,GeoPlayground.getHght()/3*i,2*SZ+fudge,GeoPlayground.getHght()/3*i);
		g.drawLine(fudge,2*SZ,2*SZ+fudge,2*SZ);
		break;
	  case 2:
	  case 3:
		g.setColor(new Color(210,210,200));
		g.drawArc(SZ*4/3+fudge,SZ-4,SZ*2/3,8,0,180);
		g.setColor(new Color(128,128,0));
		g.drawArc(SZ*4/3+fudge,SZ-4,SZ*2/3,8,180,180);
		g.setColor(new Color(128,0,128));
		g.drawOval(fudge,0,2*SZ,2*SZ);
		g.setColor(Color.black);
		g.drawOval(SZ*2/3+fudge,SZ*2/3,SZ*2/3+1,SZ*2/3+1);
		break;
	}
  }
  public double[] convertMousetoCoord(MouseEvent arg0,int SZ){
    double[] v={0,0,0};
	int cs=GeoPlayground.getHght();
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	v[0]=(double)arg0.getX()-fudge;
    v[1]=(double)arg0.getY();
    v[2]=0.;
    switch (GeoPlayground.model){
	  case 0:
	    for (int i=0;i<2;i++) v[i]/=cs;
	    break;
	  case 1:
		for (int i=0;i<2;i++) {v[i]/=(cs/3); v[i]=v[i]-(int)v[i];}
		break;
	  case 2:
	  case 3:
		double[] c={SZ,SZ,0};
		if (MathEqns.norm(c,v)<SZ/3 || MathEqns.norm(c,v)>SZ) v[2]=-1;
		v[0]=3*(v[0]-SZ)/SZ;v[1]=3*(v[1]-SZ)/SZ;
		double theta=Math.atan(v[1]/v[0]);
		if (v[0]<0) theta+=Math.PI;
		double phi=Math.acos(MathEqns.norm(v)-2);
		v[0]=theta/Math.PI/2; while(v[0]<0)v[0]+=1;
		v[1]=phi/Math.PI/2;
		break;
	}
	return v;
  }
  
  public boolean mouseIsValid(double[] v){
    if (GeoPlayground.model==0 && v[0]>GeoPlayground.getHght()) return false;
	if (GeoPlayground.model==2 && v[2]==-1) return false; 
	return true;
  }

  public GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1){
		switch(Type){
		  case POINT: return new ToroidalPoint(Type,clickedList,vector1);
		  case PTonLINE: return new ToroidalPTonLINE(Type,clickedList,vector1);
		  case PTonCIRC: return new ToroidalPTonCIRC(Type,clickedList,vector1);
		  case MIDPT:return new ToroidalMIDPT(Type,clickedList,vector1);
		  case FIXedPT: return new ToroidalFIXedPT(Type,clickedList,vector1);
		  //case REFLECT_PT: return new ToroidalReflectPt(Type,clickedList,vector1); //(not valid in Toroidal)
		  //case ROTATE_PT: return new ToroidalRotatePt(Type,clickedList,vector1);   //(not valid in Toroidal)
		  case TRANSLATE_PT: return new ToroidalTranslatePt(Type,clickedList,vector1);
		  //case INVERT_PT: return new ToroidalInvertPt(Type,clickedList,vector1);
		  case DISTANCE: return new ToroidalDISTANCE(Type,clickedList,vector1);
		  case ANGLE: return new ToroidalANGLE(Type,clickedList,vector1);
		  case CIRCUMF: return new ToroidalCIRCUMF(Type,clickedList,vector1);
		  case AREA: return new ToroidalAREA(Type,clickedList,vector1);		  
		  case RATIO: return new ToroidalRATIO(Type,clickedList,vector1);		  
		  case SUM:		  
		  case DIFF:
		  case PROD: return new ToroidalSUM(Type,clickedList,vector1);
		  case COMMENT: return new ToroidalCOMMENT(Type,clickedList,vector1);
		  case TRIANGLE: return new ToroidalTRIANGLE(Type,clickedList,vector1);	
		  case CONSTANT: return new ToroidalCONSTANT(Type,clickedList,vector1);
		  //case LINEintLINE0:
		  //case LINEintLINE1: return new ToroidalLINEintLINE(Type,clickedList,vector1);
		  //case CIRCintLINE0:
		  //case CIRCintLINE1: return new ToroidalCIRCintLINE(Type,clickedList,vector1);
		  //case CIRCintCIRC00:
		  //case CIRCintCIRC01:
		  //case CIRCintCIRC10:
		  //case CIRCintCIRC11: return new ToroidalCIRCintCIRC(Type,clickedList,vector1);		  
		  default: 
		    return null;
		}
}
  
  public GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList){
		 switch(Type){
		   case RAY: Type=LINE;
		   case LINE: return new ToroidalLine(Type,clickedList);
		   case PERP: return new ToroidalPERP(Type,clickedList);
		   case SEGMENT: return new ToroidalSEGMENT(Type,clickedList);
		   case BISECTOR: return new ToroidalBISECTOR(Type,clickedList);
		   case PARLL0: return new ToroidalPARLL0(Type,clickedList);
		   default:
			  	return null;
		 }
  }
  public GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList){
    return new ToroidalCircle(Type,clickedList);
  }
  public void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list){
    for(int m=0;m<2;m++){
      ToroidalPoint newPoint;
      newPoint=((ToroidalConstruct)clickedList.get(0)).intersect(m,(ToroidalConstruct)clickedList.get(1));
      list.addLast(newPoint);
      list.getLast().setID(list.size()-1);
      list.getLast().update();
      double[] v={0,0,0};
      list.getLast().getNewXYZ(v);	list.getLast().setXYZ(v);
      list.getLast().setValid(list.getLast().getValidNew());
      if(clickedList.get(1).getType()!=GeoConstruct.CIRCLE&&clickedList.get(0).getType()!=GeoConstruct.CIRCLE) break;
    }
  }
  public double getScale(){return ToroidalConstruct.getScale();}
  public void setScale(double s){ToroidalConstruct.setScale(s);}
  
  public String extension(){return "tor";}
  
  public String getName(){return "Toroidal";}
  
  public FileFilter getFileFilter(){
    return new FileNameExtensionFilter("Toroidal file", "tor");
  }
}

/************************************************************************************************************************/
class ConicalGeometry extends GeoMetry{
  public int getGeometry() {return 6;}
  public void drawModel(Graphics g,int SZ){
	for (int i=0;i<4;i++) {
	  GeoPlayground.modelMI[i].setText(GeoPlayground.modelText[6][i]);  
	  GeoPlayground.modelMI[i].setEnabled(i<3);
	  GeoPlayground.modelMI[i].setVisible(i<3);
	}
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	switch (GeoPlayground.model) {
	  case 0:
		g.setColor(Color.black);
		g.fillOval(SZ+fudge-1,SZ-1,2,2);
        break;
	  case 1:
		g.setColor(new Color(230,230,230));
		g.fillRect(0,0,SZ+fudge,2*SZ+fudge);
		if (ConicalConstruct.scale>2) {
			double theta=Math.PI/2.-2*Math.PI/ConicalConstruct.scale;
			int[] x={SZ+fudge,SZ+fudge,GeoPlayground.getWdth(),GeoPlayground.getWdth()},y={SZ,0,0,(int)(SZ+(SZ+fudge)*Math.tan(theta))};
			g.fillPolygon(x,y,4);
		}
		g.setColor(Color.black);
		for (int i=0;i<ConicalConstruct.scale;i++) g.drawLine(SZ+fudge, SZ,
				(int)(SZ+fudge+3*SZ*Math.sin(i*Math.PI*2/ConicalConstruct.scale)),
				(int)(SZ+3*SZ*Math.cos(i*Math.PI*2/ConicalConstruct.scale)));
		break;
	  case 2:
	  case 3:
		g.setColor(Color.black);
		g.drawLine(SZ+fudge,0,(int)(SZ+fudge+3*SZ/ConicalConstruct.scale),(int)(3*SZ*Math.sqrt(1.-1./(ConicalConstruct.scale*ConicalConstruct.scale))));
		g.drawLine(SZ+fudge,0,(int)(SZ+fudge-3*SZ/ConicalConstruct.scale),(int)(3*SZ*Math.sqrt(1.-1./(ConicalConstruct.scale*ConicalConstruct.scale))));
		break;
	}
  }
  public double[] convertMousetoCoord(MouseEvent arg0,int SZ){
    double[] v={0,0,0},w={0,0,0};
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	if (GeoPlayground.getHght()>GeoPlayground.getWdth()) fudge=0;
	v[0]=(double)arg0.getX()-fudge;
    v[1]=(double)arg0.getY();
    v[2]=0.;
    w[0]=v[0]-SZ;
    w[1]=v[1];
    w[2]=v[2];
    double rad=0.,arg=0.;
    switch (GeoPlayground.model){
	  case 0:
		  w[1]-=SZ;
		  rad=MathEqns.norm(w);
		  arg=ConicalConstruct.arctan(w[1],w[0])/ConicalConstruct.scale;
		  if (arg<0) arg+=2*Math.PI/ConicalConstruct.scale;
		  v[0]=rad*Math.sin(arg);
		  v[1]=rad*Math.cos(arg);
		  break;
	  case 1:
		  v[0]-=SZ; v[1]-=SZ;
		  break;
	  case 2:
	  case 3:
		  if (Math.acos(v[1]/MathEqns.norm(w))>Math.asin(1./ConicalConstruct.scale)) v[2]=-1;
		  else {
			  v[1]/=2;		w[0]/=2;
			  rad=v[1]/Math.sqrt(1.-1./(ConicalConstruct.scale*ConicalConstruct.scale));
			  arg=Math.asin(w[0]/(rad/ConicalConstruct.scale))/ConicalConstruct.scale;
			  v[0]=rad*Math.sin(arg);
			  v[1]=rad*Math.cos(arg);
		  }
		break;
	}
	return v;
  }
  
  public boolean mouseIsValid(double[] v){
	if (GeoPlayground.model==2 && v[2]==-1) return false; 
	return true;
  }

  public GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1){
		switch(Type){
		  case POINT: return new ConicalPoint(Type,clickedList,vector1);
		  case PTonLINE: return new ConicalPTonLINE(Type,clickedList,vector1);
		  case PTonCIRC: return new ConicalPTonCIRC(Type,clickedList,vector1);
		  case MIDPT:return new ConicalMIDPT(Type,clickedList,vector1);
		  case FIXedPT: return new ConicalFIXedPT(Type,clickedList,vector1);
		  //case REFLECT_PT: return new ConicalReflectPt(Type,clickedList,vector1); //(not valid in Conical)
		  //case ROTATE_PT: return new ConicalRotatePt(Type,clickedList,vector1);   //(not valid in Conical)
		  //case TRANSLATE_PT: return new ConicalTranslatePt(Type,clickedList,vector1);
		  //case INVERT_PT: return new ConicalInvertPt(Type,clickedList,vector1);
		  case DISTANCE: return new ConicalDISTANCE(Type,clickedList,vector1);
		  case ANGLE: return new ConicalANGLE(Type,clickedList,vector1);
		  case CIRCUMF: return new ConicalCIRCUMF(Type,clickedList,vector1);
		  case AREA: return new ConicalAREA(Type,clickedList,vector1);		  
		  case RATIO: return new ConicalRATIO(Type,clickedList,vector1);		  
		  case SUM:		  
		  case DIFF:
		  case PROD: return new ConicalSUM(Type,clickedList,vector1);
		  case COMMENT: return new ConicalCOMMENT(Type,clickedList,vector1);
		  case TRIANGLE: return new ConicalTRIANGLE(Type,clickedList,vector1);	
		  case CONSTANT: return new ConicalCONSTANT(Type,clickedList,vector1);
		  //case LINEintLINE0:
		  //case LINEintLINE1: return new ConicalLINEintLINE(Type,clickedList,vector1);
		  //case CIRCintLINE0:
		  //case CIRCintLINE1: return new ConicalCIRCintLINE(Type,clickedList,vector1);
		  //case CIRCintCIRC00:
		  //case CIRCintCIRC01:
		  //case CIRCintCIRC10:
		  //case CIRCintCIRC11: return new ConicalCIRCintCIRC(Type,clickedList,vector1);		  
		  default: 
		    return null;
		}
}
  
  public GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList){
		 switch(Type){
		   case RAY: return new ConicalLine(Type,clickedList);
		   case LINE: return new ConicalLine(Type,clickedList);
		   case PERP: return new ConicalPERP(Type,clickedList);
		   case SEGMENT: return new ConicalSEGMENT(Type,clickedList);
		   case BISECTOR: return new ConicalBISECTOR(Type,clickedList);
		   //case PARLL0: return new ConicalPARLL0(Type,clickedList);
		   default:
			  	return null;
		 }
  }
  public GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList){
    return new ConicalCircle(Type,clickedList);
  }
  public void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list){
    for(int m=0;m<2;m++){
      ConicalPoint newPoint;
      newPoint=((ConicalConstruct)clickedList.get(0)).intersect(m,(ConicalConstruct)clickedList.get(1));
      list.addLast(newPoint);
      list.getLast().setID(list.size()-1);
      list.getLast().update();
      double[] v={0,0,0};
      list.getLast().getNewXYZ(v);	list.getLast().setXYZ(v);
      list.getLast().setValid(list.getLast().getValidNew());
      if(clickedList.get(1).getType()!=GeoConstruct.CIRCLE&&clickedList.get(0).getType()!=GeoConstruct.CIRCLE) break;
    }
  }
  public double getScale(){return ConicalConstruct.getScale();}
  public void setScale(double s){ConicalConstruct.setScale(s);}
  
  public String extension(){return "con";}
  
  public String getName(){return "Conical";}
  
  public FileFilter getFileFilter(){
    return new FileNameExtensionFilter("Conical file", "con");
  }
}

/************************************************************************************************************************/
class HyperbolicGeometry extends GeoMetry{
  public int getGeometry() {return 3;}
  public void drawModel(Graphics g,int SZ){
	for (int i=0;i<4;i++) {
	  GeoPlayground.modelMI[i].setText(GeoPlayground.modelText[3][i]);
	  GeoPlayground.modelMI[i].setEnabled(true);
	  GeoPlayground.modelMI[i].setVisible(true);
	}
	g.setColor(Color.black);
	int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
	switch (GeoPlayground.model) {
	  case 3:
	    break;
	  case 2:
	    g.drawLine(0,2*SZ,GeoPlayground.getWdth(),2*SZ);
        HyperConstruct.resetScale();
        break;
	  case 0:
	  case 1:
        HyperConstruct.resetScale();
        g.drawOval(0+fudge,0,SZ*2,SZ*2);      // playing field
	    break;
	}
  
  }
  
  public double[] convertMousetoCoord(MouseEvent arg0,int SZ){
    double[] v={0,0,0};
    int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    v[0]=(double)(arg0.getX()-SZ-fudge)/SZ;
    v[1]=(double)(arg0.getY()-SZ)/SZ;
    HyperConstruct.unscale(v);
    v[2]=Math.sqrt(1+v[0]*v[0]+v[1]*v[1]);
    switch (GeoPlayground.model) {
	  case 3:
	    break;
	  case 2:									// this is the conversion from UHP model
	    double xx=v[0],yy=v[1];					// to Poincare Disc Model. Our UHP model
		v[0]=2*xx/(xx*xx+(yy-2)*(yy-2));		// is non-standard: div. by 2 & shifted
		v[1]=-2*(yy-2)/(xx*xx+(yy-2)*(yy-2))-1;	// down to fit the screen well.
	  case 0:
	    if (v[0]*v[0]+v[1]*v[1]>=1) {
		  v[0]=0;v[1]=0;v[2]=-2;				
		}
		else {
	      double d=1/(1-v[0]*v[0]-v[1]*v[1]);	// conversion from Poincare Disc Model
	      v[2]=(1+v[0]*v[0]+v[1]*v[1])*d;		// to Minkowski-Weierstrass Model
		  v[0]*=(2*d);							//
		  v[1]*=(2*d);							//
		}
	    break;
	  case 1:
	    if (v[0]*v[0]+v[1]*v[1]>=1) {
		  v[0]=0;v[1]=0;v[2]=-2;
		}
		else {
	      v[2]=1/Math.sqrt(1-v[0]*v[0]-v[1]*v[1]);	// conversion from Beltrami-Klein 
		  v[0]*=v[2];								// to Minkowski-Weierstrass Model
		  v[1]*=v[2];								//
		}
	    break;
	}
	return v;
  }
  
  public boolean mouseIsValid(double[] vector1){
    return (vector1[2]>-1.5);
  }
  
  public GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1){
		switch(Type){
		  case POINT: return new HyperPoint(Type,clickedList,vector1);
		  case PTonLINE: return new HyperPTonLINE(Type,clickedList,vector1);
		  case PTonCIRC: return new HyperPTonCIRC(Type,clickedList,vector1);
		  case MIDPT:return new HyperMIDPT(Type,clickedList,vector1);
		  case FIXedPT: return new HyperFIXedPT(Type,clickedList,vector1);
		  case REFLECT_PT: return new HyperReflectPt(Type,clickedList,vector1);
		  case ROTATE_PT: return new HyperRotatePt(Type,clickedList,vector1);
		  case TRANSLATE_PT: return new HyperTranslatePt(Type,clickedList,vector1);
		  case INVERT_PT: return new HyperInvertPt(Type,clickedList,vector1);
		  case DISTANCE: return new HyperDISTANCE(Type,clickedList,vector1);
		  case ANGLE: return new HyperANGLE(Type,clickedList,vector1);
		  case CIRCUMF: return new HyperCIRCUMF(Type,clickedList,vector1);
		  case AREA: return new HyperAREA(Type,clickedList,vector1);
		  case RATIO: return new HyperRATIO(Type,clickedList,vector1);
		  case SUM:
		  case DIFF:
		  case PROD:  return new HyperSUM(Type,clickedList,vector1);
		  case COMMENT:  return new HyperCOMMENT(Type,clickedList,vector1);
		  case TRIANGLE: return new HyperTRIANGLE(Type,clickedList,vector1);
		  case CONSTANT: return new HyperCONSTANT(Type,clickedList,vector1);
		  case LINEintLINE0:
		  case LINEintLINE1:return new HyperLINEintLINE(Type,clickedList,vector1);
		  case CIRCintLINE0:
		  case CIRCintLINE1:return new HyperCIRCintLINE(Type,clickedList,vector1);
		  case CIRCintCIRC00:
		  case CIRCintCIRC01:
		  case CIRCintCIRC10:
		  case CIRCintCIRC11:return new HyperCIRCintCIRC(Type,clickedList,vector1);
		  default: 
		    return null;
		}
  }

  public GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList){
		 switch(Type){
		   case LINE: return new HyperLine(Type,clickedList);
		   case PERP: return new HyperPERP(Type,clickedList);
		   case SEGMENT: return new HyperSEGMENT(Type,clickedList);
		   case BISECTOR: return new HyperBISECTOR(Type,clickedList);
		   case PARLL0: return new HyperPARLL0(Type,clickedList);
		   case PARLL1: return new HyperPARLL1(Type,clickedList);
		   case RAY: return new HyperLine(Type,clickedList);
		   default:
			  	return null;
		 }
  }

  public GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList){
    return new HyperCircle(Type,clickedList);
  }
  
  public void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list){
    for(int m=0;m<2;m++){
      HyperPoint newPoint;
      newPoint=((HyperConstruct)clickedList.get(0)).intersect(m,(HyperConstruct)clickedList.get(1));
      list.addLast(newPoint);
      list.getLast().setID(list.size()-1);
      list.getLast().update();
      double[] v={0,0,0};
      list.getLast().getNewXYZ(v);	list.getLast().setXYZ(v);
      list.getLast().setValid(list.getLast().getValidNew());
	  if(clickedList.get(1).getType()!=GeoConstruct.CIRCLE&&clickedList.get(0).getType()!=GeoConstruct.CIRCLE) break;
    }
  }

  public double getScale(){return HyperConstruct.getScale();}
  public void setScale(double s){HyperConstruct.setScale(s);}  

  public String extension(){return "hyp";}
  
  public String getName(){return "Hyperbolic";}
  
  public FileFilter getFileFilter(){
    return new FileNameExtensionFilter("Hyperbolic file", "hyp");
  }

}

/************************************************************************************************************************/
class ManhattanGeometry extends GeoMetry{
  public int getGeometry() {return 4;}
  public void drawModel(Graphics g,int SZ){
	for (int i=0;i<4;i++) {
	  GeoPlayground.modelMI[i].setText(GeoPlayground.modelText[4][i]);  
	  GeoPlayground.modelMI[i].setEnabled(i<1);
	  GeoPlayground.modelMI[i].setVisible(i<1);
	}
  }
  public double[] convertMousetoCoord(MouseEvent arg0,int SZ){
    double[] vector1={0,0,0};
    int fudge=(GeoPlayground.getWdth()-GeoPlayground.getHght())/2;
    vector1[0]=(double)(arg0.getX()-SZ-fudge)/SZ;
    vector1[1]=(double)(arg0.getY()-SZ)/SZ;
    vector1[2]=0.;
    return vector1;
  }
  
  public boolean mouseIsValid(double[] vector1){
    return true;
  }

   public GeoConstruct createPoint(int Type,LinkedList<GeoConstruct> clickedList,double[] vector1){
	switch(Type){
	  case POINT: return new ManhattanPoint(Type,clickedList,vector1);
	  case PTonLINE: return new ManhattanPTonLINE(Type,clickedList,vector1);
	  case PTonCIRC: return new ManhattanPTonCIRC(Type,clickedList,vector1);
	  case MIDPT:return new ManhattanMIDPT(Type,clickedList,vector1);
	  case FIXedPT: return new ManhattanFIXedPT(Type,clickedList,vector1);
	  case REFLECT_PT: return new ManhattanReflectPt(Type,clickedList,vector1);
	  case ROTATE_PT: return new ManhattanRotatePt(Type,clickedList,vector1);
	  case TRANSLATE_PT: return new ManhattanTranslatePt(Type,clickedList,vector1);
	  case INVERT_PT: return new ManhattanInvertPt(Type,clickedList,vector1);
	  case DISTANCE: return new ManhattanDISTANCE(Type,clickedList,vector1);
	  case ANGLE: return new ManhattanANGLE(Type,clickedList,vector1);
	  case CIRCUMF: return new ManhattanCIRCUMF(Type,clickedList,vector1);
	  case AREA: return new ManhattanAREA(Type,clickedList,vector1);
	  case RATIO: return new ManhattanRATIO(Type,clickedList,vector1);
	  case SUM:
	  case DIFF:
	  case PROD:  return new ManhattanSUM(Type,clickedList,vector1);
	  case COMMENT:  return new ManhattanCOMMENT(Type,clickedList,vector1);
	  case TRIANGLE: return new ManhattanTRIANGLE(Type,clickedList,vector1);
	  case CONSTANT: return new ManhattanCONSTANT(Type,clickedList,vector1);
	  case LINEintLINE0:
	  case LINEintLINE1:return new ManhattanLINEintLINE(Type,clickedList,vector1);
	  case CIRCintLINE0:
	  case CIRCintLINE1:return new ManhattanCIRCintLINE(Type,clickedList,vector1);
	  case CIRCintCIRC00:
	  case CIRCintCIRC01:
	  case CIRCintCIRC10:
	  case CIRCintCIRC11:return new ManhattanCIRCintCIRC(Type,clickedList,vector1);
	  default: 
	    return null;
	}
  }
  public GeoConstruct createLine(int Type,LinkedList<GeoConstruct> clickedList){
	 switch(Type){
	   case LINE: return new ManhattanLine(Type,clickedList);
	   case PERP: return new ManhattanPERP(Type,clickedList);
	   case SEGMENT: return new ManhattanSEGMENT(Type,clickedList);
	   case BISECTOR: return new ManhattanBISECTOR(Type,clickedList);
	   case PARLL0: return new ManhattanPARLL0(Type,clickedList);
	   case RAY: return new ManhattanLine(Type,clickedList);
	   default:
		  	return null;
	 }
  }
  public GeoConstruct createCircle(int Type,LinkedList<GeoConstruct> clickedList){
    return new ManhattanCircle(Type,clickedList);
  }
  public void createIntersections(LinkedList<GeoConstruct>clickedList,LinkedList<GeoConstruct>list){
	  for(int m=0;m<2;m++){
	      ManhattanPoint newPoint;
	      newPoint=((ManhattanConstruct)clickedList.get(0)).intersect(m,(ManhattanConstruct)clickedList.get(1));
	      list.addLast(newPoint);
	      list.getLast().setID(list.size()-1);
	      list.getLast().update();
	      double[] v={0,0,0};
	      list.getLast().getNewXYZ(v);	list.getLast().setXYZ(v);
	      list.getLast().setValid(list.getLast().getValidNew());
	      if(clickedList.get(1).getType()!=GeoConstruct.CIRCLE&&clickedList.get(0).getType()!=GeoConstruct.CIRCLE) break;
	  }
  }
	  
  public double getScale(){return ManhattanConstruct.getScale();}
  public void setScale(double s){ManhattanConstruct.setScale(s);}
  
  public String extension(){return "man";}
  
  public String getName(){return "Manhattan";}
  
  public FileFilter getFileFilter(){
    return new FileNameExtensionFilter("Manhattan file", "man");
  }
}
