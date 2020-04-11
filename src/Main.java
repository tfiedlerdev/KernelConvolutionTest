import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Main {

    static BufferedImage in_img;
    static File randomSearchOutputFolder =  new File("C:\\Users\\tfied\\Pictures\\kernelTest");
    public static void main(String[] args) throws IOException {

        in_img = ImageIO.read(new File("files/images/dog_image.jpg"));


        int[][] schärfungsFilter_kernelMatrix= new int[][]{{0,-1,0},{-1,5,-1},{0,-1,0}};
        int[][] medianFilter_kernelMatrix= new int[][]{{1,1,1},{1,1,1},{1,1,1}};
        int[][] gaussian_kernelMatrix= new int[][]{{1,2,1},{2,4,2},{1,2,1}};
        int[][] laplace_kernelMatrix= new int[][]{{0,1,0},{1,-4,1},{0,1,0}};
        int[][] relieffilter_kernelMatrix= new int[][]{{-2,-1,0},{-1,1,1},{0,1,2}};
        int[][] kernelMatrix_5x5_1= new int[][]{{-3,-2,0,-2,-3},{-2,-1,0,-1,-2},{0,-1,1,1,0},{2,1,0,1,2},{3,2,0,2,3}};
        int[][] kernelMatrix_5x5_2= new int[][]{{2,-2,0,-2,-5},{-4,-1,7,-1,-2},{1,-1,1,1,2},{2,1,0,1,2},{3,2,0,2,3}};
        int[][] kernelMatrix_5x5_3= new int[][]{{1,2,3,2,1},{2,-1,4,-1,-2},{3,-1,5,-1,3},{2,-1,4,-1,2},{1,2,3,2,1}};

        int[][] horLine_kernelMatrix= new int[][]{{-1,-1,-1},{2,2,2},{-1,-1,-1}};
        int[][] verLine_kernelMatrix= new int[][]{{-1,2,-1},{-1,2,-1},{-1,2,-1}};
        int[][] diagLine1_kernelMatrix= new int[][]{{-1,-1,2},{-1,2,-1},{2,-1,-1}};
        int[][] diagLine2_kernelMatrix= new int[][]{{2,-1,-1},{-1,2,-1},{-1,-1,2}};

        int[][] randomSearch1_kernelMatrix= new int[][]{{1,0,-1,0,-1},{0,0,-1,-1,-1},{0,-1,-1,-1,0},{0,1,1,0,-1},{0,0,0,1,0}};
        int[][] randomSearch2_kernelMatrix= new int[][]{{0,1,-1,1,-1},{0,-1,1,-1,0},{-1,0,1,-1,0},{0,0,0,-1,0},{1,0,0,0,0}};

        BufferedImage out_img =   applyKernelToImage(in_img, laplace_kernelMatrix);//applyKernelToImage(applyKernelToImage(in_img, laplace_kernelMatrix), gaussian_kernelMatrix);


        showImg(in_img, 10);
        showImg(applyKernelToImage(in_img, randomSearch2_kernelMatrix), 10+in_img.getWidth());

        randomSearch(in_img, 3,3, 10);
    }
    public static void randomSearch(BufferedImage input_image, int matrixWidth,int matrixHeight, int iterations){
        for(int i=0; i< iterations;i++){
            int[][] matrix = getRandomKernelMatrix(matrixWidth,matrixHeight,-2,2);
            BufferedImage out = applyKernelToImage(input_image,matrix);
            try {
                ImageIO.write(out,"png", new File(randomSearchOutputFolder, i+"_"+matrixToString(matrix)+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String matrixToString(int[][] matrix){
        StringBuilder sb = new StringBuilder();
        for(int x=0; x< matrix.length;x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                sb.append(matrix[x][y]).append("x");
            }
            sb.append("#");
        }
        return sb.toString();
    }
    public static int[][] getRandomKernelMatrix(int width, int height, int rangeMin, int rangeMax){
        int[][] matrix = new int[width][height];
        for(int x=0; x< width;x++){
            for(int y=0; y< height;y++){
                matrix[x][y] = (int)(rangeMin+(((float)(rangeMax-rangeMin))*Math.random()));
            }
        }
        return matrix;
    }
    public static BufferedImage maxPooling2x2(BufferedImage in_img, int strides_width, int strides_height){
        BufferedImage out_img = new BufferedImage(in_img.getWidth()/strides_width, in_img.getHeight()/strides_height, BufferedImage.TYPE_INT_ARGB);
        for(int x=0; x<out_img.getWidth(); x++){
            for(int y=0; y<out_img.getHeight(); y++){
                int newPixelVal = getMaxValOfPixelsAround(in_img, x*strides_width,y*strides_height);
                out_img.setRGB(x,y, newPixelVal);
            }
        }
        return out_img;
    }
    public static BufferedImage applyKernelToImage(BufferedImage in_img, int[][] kernelMatrix){
        long startTime = System.currentTimeMillis();
        BufferedImage out_img = new BufferedImage(in_img.getWidth(), in_img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int x=0; x<out_img.getWidth(); x++){
            for(int y=0; y<out_img.getHeight(); y++){
                int newPixelVal = getScalarProductOfPixelsAround(in_img, x,y, kernelMatrix);
                if(new Color(newPixelVal).equals(Color.WHITE)){
                    newPixelVal = Color.TRANSLUCENT;
                }

                out_img.setRGB(x,y, newPixelVal);
                //System.out.println("x: "+x+" y: "+y+" -> "+newPixelVal);
            }
        }
        System.out.println("Applying kernel to image took "+(System.currentTimeMillis()-startTime)+"ms");
        return out_img;
    }
    public static int getScalarProductOfPixelsAround(BufferedImage image, int x, int y, int[][] matrix){
        int val =0;
        for(int mx=0; mx<matrix.length; mx++){
            for(int my=0; my<matrix[0].length; my++){
                int imX = mx+x-1;
                int imY = my+y-1;
                if(imX<0){
                    imX=0;
                }
                if(imY<0){
                    imY=0;
                }
                if(imX>=image.getWidth()){
                    imX = image.getWidth()-1;
                }
                if(imY>=image.getHeight()){
                    imY = image.getHeight()-1;
                }
                val+=matrix[mx][my]*image.getRGB(imX,imY);
            }
        }
        return val;
    }
    public static int getMaxValOfPixelsAround(BufferedImage image, int x, int y){
        int Val3 = Integer.MIN_VALUE;
        int Val2 = Integer.MIN_VALUE;
        int Val4 =Integer.MIN_VALUE;

        int Val1 = image.getRGB(x,y);
        if(y<image.getHeight()-1) {
            Val3 = image.getRGB(x, y + 1);
        }
         if(x<image.getWidth()-1) {
             Val2 = image.getRGB(x + 1, y);
         }
        if(y<image.getHeight()-1&&x<image.getWidth()-1) {
            Val4 = image.getRGB(x + 1, y + 1);
        }
        return  Math.max(Val4,Math.max(Val3,Math.max(Val1, Val2)));
    }
    public static void showImg(BufferedImage bufferedImage, int x){
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        JLabel label = new JLabel( new ImageIcon(bufferedImage) );
        dialog.add( label );
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocation(x, 100);
    }
    //https://stackoverflow.com/questions/22178978/java-colour-detection
    public void colorComponents(){
        int newPixelVal=0;
        float hsb[] = new float[3];
        int r = (newPixelVal >> 16) & 0xFF;
        int g = (newPixelVal >>  8) & 0xFF;
        int b = (newPixelVal      ) & 0xFF;
        float[] HSB = Color.RGBtoHSB(r, g, b, hsb);
        if(hsb[1] < 0.1 && hsb[2] > 0.9) {
            //nearlyWhite();
        }
        else if (hsb[2] < 0.1) {
            //nearlyBlack();
            // newPixelVal = Color.BLACK.getRGB();
        }
        else {
            float deg = hsb[0]*360;
            if      (deg >=   0 && deg <  30){
                //red();
                newPixelVal = Color.WHITE.getRGB();
            }
            else if (deg >=  30 && deg <  90) {
                //yellow();
                newPixelVal = Color.WHITE.getRGB();
            }
            else if (deg >=  90 && deg < 150){
                //green();
            }
            else if (deg >= 150 && deg < 210) {
                //cyan();
            }
            else if (deg >= 210 && deg < 270){
                // blue();
            }
            else if (deg >= 270 && deg < 330) {
                //magenta();
                //  newPixelVal = Color.WHITE.getRGB();
            }
            else{
                //newPixelVal = Color.WHITE.getRGB();
                //red();
            }
        }
    }
}
