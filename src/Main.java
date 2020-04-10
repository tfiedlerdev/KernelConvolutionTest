import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Main {

    static BufferedImage in_img;
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
        BufferedImage out_img =   applyKernelToImage(applyKernelToImage(applyKernelToImage(in_img, laplace_kernelMatrix), gaussian_kernelMatrix), laplace_kernelMatrix);


        showImg(in_img, 10);
        showImg(out_img, 10+in_img.getWidth());
    }
    public static BufferedImage applyKernelToImage(BufferedImage in_img, int[][] kernelMatrix){
        long startTime = System.currentTimeMillis();
        BufferedImage out_img = new BufferedImage(in_img.getWidth(), in_img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int x=0; x<out_img.getWidth(); x++){
            for(int y=0; y<out_img.getHeight(); y++){
                int newPixelVal = getScalarProductOfPixelsAround(in_img, x,y, kernelMatrix);
                out_img.setRGB(x,y, newPixelVal);
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
    public static void showImg(BufferedImage bufferedImage, int x){
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        JLabel label = new JLabel( new ImageIcon(bufferedImage) );
        dialog.add( label );
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocation(x, 100);
    }
}
