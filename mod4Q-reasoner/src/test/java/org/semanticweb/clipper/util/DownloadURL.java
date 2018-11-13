package org.semanticweb.clipper.util;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

import static java.io.FileDescriptor.in;

/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net
 *
 */
public class DownloadURL {
    private static final int BUFFER_SIZE = 4096;

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }



    @Test
    public void download() throws IOException {
        String Path="http://www.cs.ox.ac.uk/isg/ontologies/UID/";
        String filename="";
        String save2Directory="/home/bato/git/mod4Q/mod4Q-reasoner/src/test/resources/ontologies/OxfordRepo";

        DownloadURL downloader = new DownloadURL();

        ArrayList<Integer> listOfFiles= new ArrayList<Integer>(Arrays.asList(
                        3,	377,	184,	534,	63,	457,	277,	611,	681,	38,	422,	227,	576,	134,	500,	333,	646,
                        4,	380,	185,	535,	65,	458,	281,	612,	683,	39,	425,	228,	577,	135,	501,	334,	647,
                        5,	381,	186,	538,	66,	464,	282,	613,	685,	40,	426,	229,	580,	151,	503,	335,	648,
                        6,	384,	187,	539,	67,	465,	286,	614,	687,	41,	428,	231,	582,	153,	504,	336,	649,
                        11,	385,	190,	542,	68,	466,	288,	615,	688,	42,	429,	233,	583,	154,	505,	337,	650,
                        12,	388,	191,	543,	69,	467,	289,	616,	691,	43,	430,	234,	584,	155,	509,	338,	651,
                        13,	389,	192,	544,	70,	470,	292,	617,	693,	44,	431,	235,	585,	156,	510,	339,	652,
                        15,	392,	194,	545,	71,	471,	293,	618,	694,	45,	432,	236,	586,	157,	511,	340,	653,
                        16,	393,	199,	546,	72,	472,	294,	619,	696,	46,	433,	238,	587,	158,	512,	341,	654,
                        17,	396,	203,	547,	73,	473,	296,	620,	757,	47,	434,	240,	588,	159,	513,	342,	655,
                        19,	397,	205,	548,	74,	474,	298,	621,	758,	48,	438,	241,	589,	160,	514,	349,	656,
                        22,	399,	206,	549,	86,	475,	299,	622,	759,	50,	439,	242,	593,	161,	515,	356,	657,
                        23,	401,	207,	550,	91,	476,	305,	623,	760,	51,	440,	244,	598,	162,	516,	357,	658,
                        25,	402,	208,	551,	92,	477,	306,	624,	761,	52,	441,	245,	599,	164,	518,	358,	661,
                        26,	403,	214,	552,	105,	478,	307,	625,	762,	53,	442,	246,	601,	169,	519,	359,	662,
                        27,	404,	215,	558,	107,	481,	308,	634,	763,	54,	443,	247,	602,	170,	520,	360,	663,
                        28,	405,	216,	559,	108,	482,	313,	635,	764,	55,	444,	255,	603,	171,	521,	362,	664,
                        29,	406,	217,	562,	113,	485,	314,	637,	766,	56,	451,	257,	604,	173,	522,	363,	667,
                        30,	407,	218,	563,	115,	491,	317,	638,	767,	57,	452,	258,	605,	174,	523,	364,	669,
                        31,	408,	219,	564,	117,	492,	325,	639,	768,	58,	453,	260,	606,	175,	524,	365,	670,
                        32,	409,	220,	565,	119,	493,	326,	640,	769,	59,	454,	261,	607,	180,	525,	366,	671,
                        33,	411,	221,	567,	123,	494,	328,	641,	770,	60,	455,	263,	608,	181,	527,	367,	672,
                        34,	414,	222,	568,	126,	495,	329,	642,	772,	61,	456,	265,	610,	182,	528,	372,	676,
                        35,	417,	224,	570,	127,	496,	330,	643,	774,	785,	787,	376,	679,	183,	529,	373,	677,
                        36,	418,	225,	574,	130,	497,	331,	644,	778,	37,	421,	226,	575,	133,	498,	332,	645,
                        784
                ));


        for(int i=3;i<797;i++){
            if(listOfFiles.contains(i))
                continue;

            filename="0000"+i;
            filename=filename.substring(filename.length()-5)+".owl";
            System.out.println("Downloading... "+Path+filename);

            downloadFile(Path+filename,save2Directory);
        }
    }

    @Test
    public void moveOxfordOntologiesWithout() throws IOException {
        String fromDirectory="/home/bato/git/mod4Q/mod4Q-reasoner/src/test/resources/ontologies/OxfordRepo/";
        String toDirectory="/home/bato/git/mod4Q/mod4Q-reasoner/src/test/resources/ontologies/OxfordRepo/noABox/";

        ArrayList<String> listOfFiles =
                new ArrayList<String>(Arrays.asList(
                        "00003.owl",	"00377.owl",	"00184.owl",	"00534.owl",	"00063.owl",	"00457.owl",	"00277.owl",	"00611.owl",	"00681.owl",	"00038.owl",	"00422.owl",	"00227.owl",	"00576.owl",	"00134.owl",	"00500.owl",	"00333.owl",	"00646.owl",
                        "00004.owl",	"00380.owl",	"00185.owl",	"00535.owl",	"00065.owl",	"00458.owl",	"00281.owl",	"00612.owl",	"00683.owl",	"00039.owl",	"00425.owl",	"00228.owl",	"00577.owl",	"00135.owl",	"00501.owl",	"00334.owl",	"00647.owl",
                        "00005.owl",	"00381.owl",	"00186.owl",	"00538.owl",	"00066.owl",	"00464.owl",	"00282.owl",	"00613.owl",	"00685.owl",	"00040.owl",	"00426.owl",	"00229.owl",	"00580.owl",	"00151.owl",	"00503.owl",	"00335.owl",	"00648.owl",
                        "00006.owl",	"00384.owl",	"00187.owl",	"00539.owl",	"00067.owl",	"00465.owl",	"00286.owl",	"00614.owl",	"00687.owl",	"00041.owl",	"00428.owl",	"00231.owl",	"00582.owl",	"00153.owl",	"00504.owl",	"00336.owl",	"00649.owl",
                        "00011.owl",	"00385.owl",	"00190.owl",	"00542.owl",	"00068.owl",	"00466.owl",	"00288.owl",	"00615.owl",	"00688.owl",	"00042.owl",	"00429.owl",	"00233.owl",	"00583.owl",	"00154.owl",	"00505.owl",	"00337.owl",	"00650.owl",
                        "00012.owl",	"00388.owl",	"00191.owl",	"00543.owl",	"00069.owl",	"00467.owl",	"00289.owl",	"00616.owl",	"00691.owl",	"00043.owl",	"00430.owl",	"00234.owl",	"00584.owl",	"00155.owl",	"00509.owl",	"00338.owl",	"00651.owl",
                        "00013.owl",	"00389.owl",	"00192.owl",	"00544.owl",	"00070.owl",	"00470.owl",	"00292.owl",	"00617.owl",	"00693.owl",	"00044.owl",	"00431.owl",	"00235.owl",	"00585.owl",	"00156.owl",	"00510.owl",	"00339.owl",	"00652.owl",
                        "00015.owl",	"00392.owl",	"00194.owl",	"00545.owl",	"00071.owl",	"00471.owl",	"00293.owl",	"00618.owl",	"00694.owl",	"00045.owl",	"00432.owl",	"00236.owl",	"00586.owl",	"00157.owl",	"00511.owl",	"00340.owl",	"00653.owl",
                        "00016.owl",	"00393.owl",	"00199.owl",	"00546.owl",	"00072.owl",	"00472.owl",	"00294.owl",	"00619.owl",	"00696.owl",	"00046.owl",	"00433.owl",	"00238.owl",	"00587.owl",	"00158.owl",	"00512.owl",	"00341.owl",	"00654.owl",
                        "00017.owl",	"00396.owl",	"00203.owl",	"00547.owl",	"00073.owl",	"00473.owl",	"00296.owl",	"00620.owl",	"00757.owl",	"00047.owl",	"00434.owl",	"00240.owl",	"00588.owl",	"00159.owl",	"00513.owl",	"00342.owl",	"00655.owl",
                        "00019.owl",	"00397.owl",	"00205.owl",	"00548.owl",	"00074.owl",	"00474.owl",	"00298.owl",	"00621.owl",	"00758.owl",	"00048.owl",	"00438.owl",	"00241.owl",	"00589.owl",	"00160.owl",	"00514.owl",	"00349.owl",	"00656.owl",
                        "00022.owl",	"00399.owl",	"00206.owl",	"00549.owl",	"00086.owl",	"00475.owl",	"00299.owl",	"00622.owl",	"00759.owl",	"00050.owl",	"00439.owl",	"00242.owl",	"00593.owl",	"00161.owl",	"00515.owl",	"00356.owl",	"00657.owl",
                        "00023.owl",	"00401.owl",	"00207.owl",	"00550.owl",	"00091.owl",	"00476.owl",	"00305.owl",	"00623.owl",	"00760.owl",	"00051.owl",	"00440.owl",	"00244.owl",	"00598.owl",	"00162.owl",	"00516.owl",	"00357.owl",	"00658.owl",
                        "00025.owl",	"00402.owl",	"00208.owl",	"00551.owl",	"00092.owl",	"00477.owl",	"00306.owl",	"00624.owl",	"00761.owl",	"00052.owl",	"00441.owl",	"00245.owl",	"00599.owl",	"00164.owl",	"00518.owl",	"00358.owl",	"00661.owl",
                        "00026.owl",	"00403.owl",	"00214.owl",	"00552.owl",	"00105.owl",	"00478.owl",	"00307.owl",	"00625.owl",	"00762.owl",	"00053.owl",	"00442.owl",	"00246.owl",	"00601.owl",	"00169.owl",	"00519.owl",	"00359.owl",	"00662.owl",
                        "00027.owl",	"00404.owl",	"00215.owl",	"00558.owl",	"00107.owl",	"00481.owl",	"00308.owl",	"00634.owl",	"00763.owl",	"00054.owl",	"00443.owl",	"00247.owl",	"00602.owl",	"00170.owl",	"00520.owl",	"00360.owl",	"00663.owl",
                        "00028.owl",	"00405.owl",	"00216.owl",	"00559.owl",	"00108.owl",	"00482.owl",	"00313.owl",	"00635.owl",	"00764.owl",	"00055.owl",	"00444.owl",	"00255.owl",	"00603.owl",	"00171.owl",	"00521.owl",	"00362.owl",	"00664.owl",
                        "00029.owl",	"00406.owl",	"00217.owl",	"00562.owl",	"00113.owl",	"00485.owl",	"00314.owl",	"00637.owl",	"00766.owl",	"00056.owl",	"00451.owl",	"00257.owl",	"00604.owl",	"00173.owl",	"00522.owl",	"00363.owl",	"00667.owl",
                        "00030.owl",	"00407.owl",	"00218.owl",	"00563.owl",	"00115.owl",	"00491.owl",	"00317.owl",	"00638.owl",	"00767.owl",	"00057.owl",	"00452.owl",	"00258.owl",	"00605.owl",	"00174.owl",	"00523.owl",	"00364.owl",	"00669.owl",
                        "00031.owl",	"00408.owl",	"00219.owl",	"00564.owl",	"00117.owl",	"00492.owl",	"00325.owl",	"00639.owl",	"00768.owl",	"00058.owl",	"00453.owl",	"00260.owl",	"00606.owl",	"00175.owl",	"00524.owl",	"00365.owl",	"00670.owl",
                        "00032.owl",	"00409.owl",	"00220.owl",	"00565.owl",	"00119.owl",	"00493.owl",	"00326.owl",	"00640.owl",	"00769.owl",	"00059.owl",	"00454.owl",	"00261.owl",	"00607.owl",	"00180.owl",	"00525.owl",	"00366.owl",	"00671.owl",
                        "00033.owl",	"00411.owl",	"00221.owl",	"00567.owl",	"00123.owl",	"00494.owl",	"00328.owl",	"00641.owl",	"00770.owl",	"00060.owl",	"00455.owl",	"00263.owl",	"00608.owl",	"00181.owl",	"00527.owl",	"00367.owl",	"00672.owl",
                        "00034.owl",	"00414.owl",	"00222.owl",	"00568.owl",	"00126.owl",	"00495.owl",	"00329.owl",	"00642.owl",	"00772.owl",	"00061.owl",	"00456.owl",	"00265.owl",	"00610.owl",	"00182.owl",	"00528.owl",	"00372.owl",	"00676.owl",
                        "00035.owl",	"00417.owl",	"00224.owl",	"00570.owl",	"00127.owl",	"00496.owl",	"00330.owl",	"00643.owl",	"00774.owl",	"00785.owl",	"00787.owl",	"00376.owl",	"00679.owl",	"00183.owl",	"00529.owl",	"00373.owl",	"00677.owl",
                        "00036.owl",	"00418.owl",	"00225.owl",	"00574.owl",	"00130.owl",	"00497.owl",	"00331.owl",	"00644.owl",	"00778.owl",	"00037.owl",	"00421.owl",	"00226.owl",	"00575.owl",	"00133.owl",	"00498.owl",	"00332.owl",	"00645.owl",
                        "00784.owl"
                        ));




        for(String fileEntry:listOfFiles)
        {

            System.out.println("Moving... "+fromDirectory+fileEntry);

            Files.move(Paths.get(fromDirectory+fileEntry), Paths.get(toDirectory+fileEntry), StandardCopyOption.REPLACE_EXISTING);

        }
    }



}