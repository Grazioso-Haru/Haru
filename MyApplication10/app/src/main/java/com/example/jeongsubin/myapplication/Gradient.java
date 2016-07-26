package com.example.jeongsubin.myapplication;

/**
 * Created by LimJoowon on 2016. 7. 13..
 */
public class Gradient {
    int startingColor=0xffffffff;
    int endingColor=0xff000000;
    int number=10;

    public void setGradient(int sc, int ec, int n){
        startingColor=sc;
        endingColor=ec;
        number=n;
    }
    public void printCondition(){
        System.out.println("----------");
        System.out.println(Integer.toHexString(startingColor));
        System.out.println(Integer.toHexString(endingColor));
        System.out.println(number);
        System.out.println("----------");
    }
    public int[] decomposeColor(){
        int[] result=new int[8];
        result[0]=(startingColor & 0xff000000)>>>24;
        result[1]=(startingColor & 0x00ff0000)>>>16;
        result[2]=(startingColor & 0x0000ff00)>>>8;
        result[3]=(startingColor & 0x000000ff);
        result[4]=(endingColor & 0xff000000)>>>24;
        result[5]=(endingColor & 0x00ff0000)>>>16;
        result[6]=(endingColor & 0x0000ff00)>>>8;
        result[7]=(endingColor & 0x000000ff);

        return result;
    }
    public int[] calColor(int[] colorComp){
        int a1, r1, g1, b1, a2, r2, g2, b2, aa, rr, gg, bb, temp;
        double p;
        int[] result=new int[number];
        a1=colorComp[0];
        r1=colorComp[1];
        g1=colorComp[2];
        b1=colorComp[3];
        a2=colorComp[4];
        r2=colorComp[5];
        g2=colorComp[6];
        b2=colorComp[7];

        for (int i=0;i<number;i++){
            p=((double)i)/(number-1);
            aa=(int) (a1*(1-p)+a2*p);
            rr=(int) (r1*(1-p)+r2*p);
            gg=(int) (g1*(1-p)+g2*p);
            bb=(int) (b1*(1-p)+b2*p);
            temp = (aa<<24) + (rr<<16) + (gg<<8) + bb;
            result[i]=temp;
        }
        return result;
    }
    public int[] getColorArray(){
        int[] colorArray = new int[number];
        int[] colorComp = decomposeColor();
        colorArray=calColor(colorComp);
        return colorArray;
    }
    public String[] getColorArrayString(){
        int[] colorArray = getColorArray();
        String[] colorArrayString = new String[number];
        for (int i=0;i<number;i++){
            colorArrayString[i]="#"+Integer.toHexString(colorArray[i]);
        }
        return colorArrayString;
    }
    public void printColors(String[] sa){
        for (int i=0;i<sa.length;i++){
            System.out.println(sa[i]);
        }
    }
    /*
     public static void main(String args[]){
     Gradient g=new Gradient();
     g.setGradient(0xffff0000, 0xff00ff00, 30);
     String[] cas = g.getColorArrayString();
     g.printColors(cas);
     }
     */
}