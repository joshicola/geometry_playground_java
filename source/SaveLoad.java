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
 
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import jpsxdec.FileNameExtensionFilter;

public final class SaveLoad{

  public static void save(LinkedList<GeoConstruct>list,GeoCanvas parent) {
	  if (GeoPlayground.lastSaveDir.equals("")) GeoPlayground.lastSaveDir=GeoPlayground.lastOpenDir;
    JFileChooser saveDlg=new JFileChooser(GeoPlayground.lastSaveDir); 
    saveDlg.setFileFilter(parent.geometry.getFileFilter());
    int retval = saveDlg.showSaveDialog(parent);
    if (retval == JFileChooser.APPROVE_OPTION) {
      //... The user selected a file, get it, use it.
      String fileName=saveDlg.getName(saveDlg.getSelectedFile());
      String dirName=saveDlg.getSelectedFile().getPath();
      PrintWriter outFile;

      if(((FileNameExtensionFilter)(parent.geometry.getFileFilter())).accept(saveDlg.getSelectedFile()))
        fileName=dirName;
      else
        fileName=dirName+"."+parent.geometry.extension();
      try {
        outFile = new PrintWriter(new FileWriter(fileName),true);
        outFile.println("Geometry:"+parent.geometry.getName());
        outFile.println("!KEY:ID;Type;<x;y;z>;{parent-1;parent-2;...};isShown;hasLabel;isReal;displayString");
        for (int i=0;i<list.size();i++){
          String strObj="";
          strObj+= list.get(i).ID+";";
          strObj+= TypeToString(list.get(i).type);
          if (list.get(i).getType()!=GeoConstruct.CONSTANT)
        	  strObj+=";<"+list.get(i).getX()+";"+list.get(i).getY()+";"+list.get(i).getZ()+">;";
          else strObj+=";<"+list.get(i).getX()+";"+list.get(i).getY()+";"+list.get(i).measureValue+">;";
          strObj+="{";
          for(int j=0;j<list.get(i).constList.size();j++){
            System.out.println(list.get(i).constList.size());
            strObj+=list.get(i).get(j).getID();
            if(j<list.get(i).constList.size()-1) 
              strObj+=";";
          }
          strObj+="};";
          strObj+=list.get(i).shown+";"+list.get(i).labelShown+";"+list.get(i).isReal;
          if (list.get(i).getType()==GeoConstruct.CONSTANT || 
        	  list.get(i).getType()==GeoConstruct.COMMENT || (list.get(i).labelShown &&
        	  (list.get(i).getType()>30) ||
        	  (list.get(i).getType()<30 && !list.get(i).displayText.equals(""+(char)('A'+i%26)+""+(i/26)))))
        	  strObj=strObj+";"+list.get(i).getDisplayText();
          if(i<(list.size()-1))strObj+="\n";
          outFile.print(strObj);
        }
        outFile.close();      
      } catch (IOException e) {
        JOptionPane.showMessageDialog(parent,"File Error: "+e.toString());

        //System.err.println("File Error: "+e.toString());
        //System.exit(1);
      }
      if (dirName.contains("/")) GeoPlayground.lastSaveDir=dirName.substring(0,dirName.lastIndexOf('/'))+"/";
      else GeoPlayground.lastSaveDir=dirName;
    }

  }
  // end save method
  //method to save canvas to a image file
  public static void saveImage(GeoCanvas parent) {
	  FileFilter ImageFilter=new FileNameExtensionFilter("Images", "jpg","gif","bmp");
	  if (GeoPlayground.lastSaveDir.equals("")) GeoPlayground.lastSaveDir=GeoPlayground.lastOpenDir;
	    JFileChooser saveDlg=new JFileChooser(GeoPlayground.lastSaveDir); 
	    saveDlg.setFileFilter(ImageFilter);
	    int retval = saveDlg.showSaveDialog(parent);
	    if (retval == JFileChooser.APPROVE_OPTION) {
	      //... The user selected a file, get it, use it.
	      String fileName=saveDlg.getName(saveDlg.getSelectedFile());
	      String dirName=saveDlg.getSelectedFile().getPath();
	      if(((FileNameExtensionFilter)(ImageFilter)).accept(saveDlg.getSelectedFile()))
	          fileName=dirName;
	        else
	          fileName=dirName+".jpg";
	      int w = parent.getWidth();
	      int h = parent.getHeight();
	      int type = BufferedImage.TYPE_INT_RGB;
	      BufferedImage image = new BufferedImage(w,h,type);
	      Graphics2D g2 = image.createGraphics();
	      g2.setColor(new Color(255,255,255));
	      g2.fillRect(0,0, w, h);
	      parent.paint(g2);
	      g2.dispose();
	      String ext = fileName.substring(fileName.length()-3) ;//"jpg";  
	      File file = new File(fileName);
	      try {
	          ImageIO.write(image, ext, file);
	      } catch(IOException e) {
	          System.out.println("write error: " + e.getMessage());
	      }
	      if (dirName.contains("/")) GeoPlayground.lastSaveDir=dirName.substring(0,dirName.lastIndexOf('/'))+"/";
	      else GeoPlayground.lastSaveDir=dirName;
	    }
  }
  // method to load info from a text file
  public static void load(LinkedList<GeoConstruct>list,GeoCanvas parent) {
	Scanner scanner=new Scanner("");
	boolean processConstruct=false;
	if (GeoPlayground.lastOpenDir.equals("")) GeoPlayground.lastOpenDir=GeoPlayground.lastSaveDir;
    try{
	  if(GeoPlayground.lastOpenDir.contains("http://")){
		URL LoadURL=new URL(GeoPlayground.lastOpenDir);
		InputStream istream=LoadURL.openStream();
		if(istream.available()>0){
	      scanner = new Scanner(istream);
	      processConstruct=true;
		}
		GeoPlayground.lastOpenDir="";
	  }
	  else{
		JFileChooser openDlg=new JFileChooser(GeoPlayground.lastOpenDir); 
		openDlg.setFileFilter(parent.geometry.getFileFilter());
		int retval = openDlg.showDialog(parent,"");								// this adds a dialogue box
	    if (retval == JFileChooser.APPROVE_OPTION) {							//	for the user to type a path
	    	String path = openDlg.getSelectedFile().getPath();					//
			//... The user selected a file, get it, use it.						//
	    	if (path.contains("http:/")) {										// if path contains http:/, it
	    		if (!path.contains("http://")){ 								//	should contain http://, so
	    			path="http://"+path.substring(path.indexOf("http:/")+6);	//	put the second / in and then
	    			GeoPlayground.lastOpenDir="";}								//  clear lastOpenDir, then
	    		URL LoadURL=new URL(path);										//	download the file from the web
	    		InputStream istream=LoadURL.openStream();						//	(JFileChooser removes the 2nd
	    		if(istream.available()>0){										//	slash)
	    	      scanner = new Scanner(istream);								//
	    	      processConstruct=true;										//
	    		}																//
	    	}																	//
	    	else {																// otherwise, load the file
	    		scanner = new Scanner(openDlg.getSelectedFile());				//	from the system
				GeoPlayground.lastOpenDir=openDlg.getSelectedFile().getPath();	//
				processConstruct=true;											//
	    	}																	//
		}
	  }
      //For the Time being, Clear the list before loading
      list.clear();
      
      if (processConstruct) {
        //... The user selected a file, get it, use it.
        try {
          list.size();
          //Check the first line for geometry
          if(processGeometry(scanner.nextLine(),parent))
            throw new IOException("The File you are trying to open does not match the geometry.");
          //first use a Scanner to get each line
          while ( scanner.hasNextLine() ){
            processLine(scanner.nextLine(),list,parent.geometry);
          }
        } 
        catch (Exception e){
          throw e;
        }
        finally {
          //ensure the underlying stream is always closed
          scanner.close();
        }

      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(parent,"File Error: "+e.toString());
    }
  }
  // end load method

  private static boolean processGeometry(String aLine, GeoCanvas parent){
    Scanner scanner = new Scanner(aLine);
    scanner.useDelimiter(":");
    scanner.next();
    String Geometry=scanner.next();
    return (!Geometry.equals(parent.geometry.getName()));
  }
  private static void processLine(String aLine,LinkedList<GeoConstruct>list, GeoMetry geometry){
    Scanner scanner = new Scanner(aLine);
    //String isComment=scanner.findInLine("!");
    if(scanner.findInLine("!")==null){
      scanner.useDelimiter(";");
      LinkedList<GeoConstruct> parentList =new LinkedList<GeoConstruct>();

      int ID=scanner.nextInt();
      String typeStr=scanner.next();
      int type=StringToType(typeStr);
      double[] vector1={0,0,0};
      String temp=scanner.next();

      vector1[0]=Double.valueOf(temp.substring(1));
      vector1[1]=scanner.nextDouble();
      temp=scanner.next();
      vector1[2]=Double.valueOf(temp.substring(0, temp.length()-1));

      /*************New Improved parentList code*****************/
      int cID;
      temp=scanner.next();
      if(!temp.equals("{}")){
        temp=temp.substring(1);
        //cID=Integer.valueOf(temp.substring(1));
        while(true){
          //parentList.addLast(list.get(cID));
          //temp=scanner.next();
          if(temp.contains("}")){
            cID=Integer.valueOf(temp.substring(0,temp.length()-1));
            parentList.addLast(list.get(cID));
            break;
          }
          else{
            cID=Integer.valueOf(temp);
            parentList.addLast(list.get(cID));
          }
          temp=scanner.next();
        }
      }
      /********************************************/
      /**********Legacy code below      
      int iA,iB=-1;
      String A=scanner.next();
      if(!A.equals("null")) iA=Integer.valueOf(A);
      String B=scanner.next();
      if(!B.equals("null")) iB=Integer.valueOf(B);
       ****************************************/
      GeoConstruct listItem=null;
      double[] v={0,0,0};
      switch(type){
      case GeoConstruct.LINE:
      case GeoConstruct.PERP:
      case GeoConstruct.PARLL0:
      case GeoConstruct.PARLL1:
      case GeoConstruct.SEGMENT:
      case GeoConstruct.RAY:
	  case GeoConstruct.BISECTOR:
        listItem=geometry.createLine(type, parentList);
		listItem.setXYZ(vector1);
        break; 

      case GeoConstruct.CIRCLE:
        listItem=geometry.createCircle(type,parentList);
		listItem.setXYZ(vector1);
        break;

      case GeoConstruct.POINT:
      case GeoConstruct.FIXedPT:
      case GeoConstruct.PTonLINE:
      case GeoConstruct.PTonCIRC:
	  case GeoConstruct.MIDPT:
      case GeoConstruct.DISTANCE:
      case GeoConstruct.CIRCUMF:
      case GeoConstruct.AREA:
      case GeoConstruct.RATIO:
      case GeoConstruct.SUM:
      case GeoConstruct.PROD:
      case GeoConstruct.COMMENT:
      case GeoConstruct.DIFF:
	  case GeoConstruct.ANGLE:
	  case GeoConstruct.TRIANGLE:
	  case GeoConstruct.LINEintLINE0:
      case GeoConstruct.CIRCintLINE0:
      case GeoConstruct.CIRCintCIRC00:
      case GeoConstruct.LINEintLINE1: 
      case GeoConstruct.CIRCintLINE1:
      case GeoConstruct.CIRCintCIRC01:
      case GeoConstruct.CIRCintCIRC10:
      case GeoConstruct.CIRCintCIRC11:
      case GeoConstruct.REFLECT_PT:
      case GeoConstruct.ROTATE_PT:
      case GeoConstruct.TRANSLATE_PT:
      case GeoConstruct.INVERT_PT:
    	for (int i=0;i<3;i++) v[i]=vector1[i];
        listItem=geometry.createPoint(type,parentList,vector1);
		listItem.setXYZ(v);
		break;
      case GeoConstruct.CONSTANT:
    	  for (int i=0;i<3;i++) v[i]=vector1[i];
          listItem=geometry.createPoint(type,parentList,vector1);
  		  break;
      }
      if(listItem!=null){
        listItem.setID(ID);
        listItem.shown=(scanner.next().equals("true"));
        listItem.labelShown=(scanner.next().equals("true"));
        listItem.isReal=(scanner.next().equals("true"));
        list.add(listItem);
        if (list.getLast().getType()>30 && list.getLast().getType()!=GeoConstruct.CONSTANT) {
        	list.getLast().measureValue=1;
        }
        if (scanner.hasNext()) list.getLast().setDisplayText(scanner.next());
      }
    }
    else{
    	scanner.nextLine();
    }
  }

  private static int StringToType(String type){
    if(type.equals("POINT")) return GeoConstruct.POINT;
    if(type.equals("PTonLINE")) return GeoConstruct.PTonLINE;
    if(type.equals("PTonCIRC")) return GeoConstruct.PTonCIRC;
    if(type.equals("LINE")) return GeoConstruct.LINE;
    if(type.equals("PERP")) return GeoConstruct.PERP;
    if(type.equals("PARALLEL0")) return GeoConstruct.PARLL0;
    if(type.equals("PARALLEL1")) return GeoConstruct.PARLL1;
    if(type.equals("SEGMENT")) return GeoConstruct.SEGMENT;
    if(type.equals("RAY")) return GeoConstruct.RAY;
    if(type.equals("CIRCLE")) return GeoConstruct.CIRCLE;
    if(type.equals("LINEintLINE0")) return GeoConstruct.LINEintLINE0;
    if(type.equals("LINEintLINE1")) return GeoConstruct.LINEintLINE1;
    if(type.equals("CIRCintLINE0")) return GeoConstruct.CIRCintLINE0;
    if(type.equals("CIRCintLINE1")) return GeoConstruct.CIRCintLINE1;
    if(type.equals("CIRCintCIRC00")) return GeoConstruct.CIRCintCIRC00;
    if(type.equals("CIRCintCIRC01")) return GeoConstruct.CIRCintCIRC01;
    if(type.equals("CIRCintCIRC10")) return GeoConstruct.CIRCintCIRC10;
    if(type.equals("CIRCintCIRC11")) return GeoConstruct.CIRCintCIRC11;
    if(type.equals("MIDPT")) return GeoConstruct.MIDPT;
    if(type.equals("FIXedPT")) return GeoConstruct.FIXedPT;
    if(type.equals("DISTANCE")) return GeoConstruct.DISTANCE;
    if(type.equals("CIRCUMFERENCE")) return GeoConstruct.CIRCUMF;
    if(type.equals("AREA")) return GeoConstruct.AREA;
    if(type.equals("RATIO")) return GeoConstruct.RATIO;
    if(type.equals("SUM")) return GeoConstruct.SUM;
    if(type.equals("PRODUCT")) return GeoConstruct.PROD;
    if(type.equals("DIFFERENCE")) return GeoConstruct.DIFF;
    if(type.equals("COMMENT")) return GeoConstruct.COMMENT;
    if(type.equals("ANGLE")) return GeoConstruct.ANGLE;
    if(type.equals("TRIANGLE")) return GeoConstruct.TRIANGLE;
	if(type.equals("BISECTOR")) return GeoConstruct.BISECTOR;
	if(type.equals("CONSTANT")) return GeoConstruct.CONSTANT;
	if(type.equals("REFLECT_PT")) return GeoConstruct.REFLECT_PT;
	if(type.equals("ROTATE_PT")) return GeoConstruct.ROTATE_PT;
	if(type.equals("TRANSLATE_PT")) return GeoConstruct.TRANSLATE_PT;
	if(type.equals("INVERT_PT")) return GeoConstruct.INVERT_PT;
    else return 200;
  }

  private static String TypeToString(int type){
    switch(type){

    case GeoConstruct.POINT:			return "POINT";
    case GeoConstruct.PTonLINE:			return "PTonLINE";
    case GeoConstruct.PTonCIRC:			return "PTonCIRC";
    case GeoConstruct.LINE:				return "LINE";
    case GeoConstruct.PERP:				return "PERP";
    case GeoConstruct.PARLL0:			return "PARALLEL0";
    case GeoConstruct.PARLL1:			return "PARALLEL1";
    case GeoConstruct.SEGMENT:			return "SEGMENT";
    case GeoConstruct.RAY:				return "RAY";
    case GeoConstruct.CIRCLE:			return "CIRCLE";
    case GeoConstruct.LINEintLINE0:		return "LINEintLINE0";
    case GeoConstruct.LINEintLINE1:		return "LINEintLINE1";
    case GeoConstruct.CIRCintLINE0:		return "CIRCintLINE0";
    case GeoConstruct.CIRCintLINE1:		return "CIRCintLINE1";
    case GeoConstruct.CIRCintCIRC00:	return "CIRCintCIRC00";
    case GeoConstruct.CIRCintCIRC01:	return "CIRCintCIRC01";
    case GeoConstruct.CIRCintCIRC10:	return "CIRCintCIRC10";
    case GeoConstruct.CIRCintCIRC11:	return "CIRCintCIRC11";
    case GeoConstruct.MIDPT:			return "MIDPT";
    case GeoConstruct.FIXedPT:			return "FIXedPT";
    case GeoConstruct.DISTANCE:			return "DISTANCE";
    case GeoConstruct.CIRCUMF:			return "CIRCUMFERENCE";
    case GeoConstruct.AREA:				return "AREA";
    case GeoConstruct.RATIO:			return "RATIO";
    case GeoConstruct.SUM:				return "SUM";
    case GeoConstruct.PROD:				return "PRODUCT";
    case GeoConstruct.DIFF:				return "DIFFERENCE";
    case GeoConstruct.COMMENT:			return "COMMENT";
	case GeoConstruct.ANGLE:			return "ANGLE";
	case GeoConstruct.TRIANGLE:			return "TRIANGLE";
	case GeoConstruct.BISECTOR:			return "BISECTOR";
	case GeoConstruct.CONSTANT:			return "CONSTANT";
	case GeoConstruct.REFLECT_PT:		return "REFLECT_PT";
	case GeoConstruct.ROTATE_PT:		return "ROTATE_PT";
	case GeoConstruct.TRANSLATE_PT:		return "TRANSLATE_PT";
	case GeoConstruct.INVERT_PT:		return "INVERT_PT";
    default: return "Nothing";
    }
  }
}