package capture;

import TestCase.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nam on 09/07/2017.
 */
public class ReadFile {
    public static List<Item> listFlow1 = new ArrayList<Item>();//luu cac goi tin dau tien cua cac flow trong 6s đầu
    public static List<Double> listIAT1 = new ArrayList<Double>();//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s đầu

    public ReadFile() throws IOException {
        listFlow1 = new ArrayList<Item>();
        listIAT1 = new ArrayList<Double>();
    }

    public static void main(String[] args) throws IOException {
        try {
            FileReader fr = new FileReader("F:\\BCA.txt");
            BufferedReader br = new BufferedReader(fr);
            String str = "";

            FileWriter fw = new FileWriter("F:\\New folder\\data_pcap\\resultBCA.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            double oldTimeStamp = 0;
            int stt = 0,s = 0;
            double itemPacket = 0;
            int dem = 1;
            Item item = null;
            Par par;
            double end = 1.493638794619E9;
            double start = end;

            while ((str = br.readLine()) != null){
                item = createItem(str);
                if(oldTimeStamp == 0){
                    oldTimeStamp = (Double) item.getFieldValue(Flow.TIME_STAMP.toString());
                }
                if (item != null) {
                    stt++;
                    System.out.println(stt);
                    itemPacket = (Double) item.getFieldValue(Flow.TIME_STAMP.toString());
                    listIAT1.add(itemPacket - oldTimeStamp);
                    oldTimeStamp = itemPacket;

                    if(itemPacket - start > 5){
                        par = new Statistics(listFlow1,listIAT1).run();
                        bw.write(dem + ",");
                        bw.write(par.getIAT() + ",");
                        bw.write(par.getPpF() + ",");
                        bw.write(par.getMaxRateProtol() + "\r\n");
                        listFlow1.clear();
                        listIAT1.clear();
                        dem++;
                        start = itemPacket;

                        if(itemPacket - end >= 300){
                            System.out.println(itemPacket +" "+ stt);
                            break;
                        }
                    }else {
                        //first là gói tin đầu tiên của luồng ứng với gói tin vừa nhận được
                        Item first = getItem1(item);

                        if (first == null) {//first = null => gói tin vừa nhận được là gói tin đầu tiên của luồng
                            listFlow1.add(item);
                        } else {
                            first.setAttribute(Flow.COUNT.toString(), (Integer) first.getFieldValue(Flow.COUNT.toString()) + 1);
                        }
                    }
                }
            }
            bw.close();
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean flowCompare(Item i,Item item) {
        if (i.getFieldValue(Flow.IP_SRC.toString()).equals(item.getFieldValue(Flow.IP_SRC.toString()))
                && i.getFieldValue(Flow.IP_DST.toString()).equals(item.getFieldValue(Flow.IP_DST.toString()))
                && i.getFieldValue(Flow.PORT_SRC.toString()).equals(item.getFieldValue(Flow.PORT_SRC.toString()))
                && i.getFieldValue(Flow.PORT_DST.toString()).equals(item.getFieldValue(Flow.PORT_DST.toString())))
            return true;

        return false;
    }

    public static Item getItem1(Item item){
        for(Item i : listFlow1){
            if(flowCompare(i,item)){
                return i;
            }
        }
        return null;
    }

    public static Item createItem(String line){

        String[] a = line.trim().split("\\t");
        Item item = new Item();
        item.setAttribute(Flow.TIME_STAMP.toString(), Double.parseDouble(a[0]));
        item.setAttribute(Flow.IP_SRC.toString(),a[1]);
        item.setAttribute(Flow.IP_DST.toString(),a[2]);
        item.setAttribute(Flow.PORT_SRC.toString(), a[3]);
        item.setAttribute(Flow.PORT_DST.toString(), a[4]);
        item.setAttribute(Flow.PROTOCOL.toString(),a[5]);
        item.setAttribute(Flow.BYTE_COUNT.toString(),Long.parseLong(a[6]));
        item.setAttribute(Flow.COUNT.toString(),Integer.valueOf(1));
        return item;
    }
}
