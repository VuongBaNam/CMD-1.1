package TestCase;

import java.io.IOException;
import java.util.*;

public class Statistics{
    private List<Item> listFlow1;//luu cac goi tin dau tien cua cac flow trong 6s đầu
    private List<Double> listIAT1;//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s đầu

    public Statistics(List<Item> listFlow1, List<Double> listIAT1 ) throws IOException {
        this.listFlow1 = listFlow1;
        this.listIAT1 = listIAT1;
    }

    public Parameter statistic() throws IOException {
        if(listFlow1.size() != 0) {

            long number_flow_one_pkt=  0;
            double RATE_ICMP = 0;
            double P_IAT = 0;
            long NUMBER_PACKET = 0;
            double PKT_SIZE_AVG = 0;
            long number_dns_respone = 0;
            double PPF = 0;

            int NUM_ICMP = 0;
            long total_byte = 0;
            //Flow nào có danh sách paket Inter-Arrival Time rỗng (kích thước = 0) thì flow đó có 1 gói tin
            for (Item item : listFlow1) {
                long numbrer_pkt_flow = (Integer) item.getFieldValue(Flow.COUNT.toString());
                if(numbrer_pkt_flow == 1){
                    number_flow_one_pkt++;
                }
                NUMBER_PACKET += numbrer_pkt_flow;
                total_byte += (Long) item.getFieldValue(Flow.BYTE_COUNT.toString());
                int pro = Integer.parseInt((String) item.getFieldValue(Flow.PORT_SRC.toString()));
                if (pro == 7) {
                    NUM_ICMP += (Integer) item.getFieldValue(Flow.COUNT.toString());
                }else if(pro == 53){
                    number_dns_respone += numbrer_pkt_flow;
                }
            }
            //ONE_PKT_FLOW là %số flow có 1 gói tin trên tổng số flow
            RATE_ICMP = NUM_ICMP * 1.0 / NUMBER_PACKET;
            PKT_SIZE_AVG = total_byte *1.0/NUMBER_PACKET;
            PPF = number_flow_one_pkt*1.0/listFlow1.size();

            int PKT_IAT_02 = 0;// số packet có inter-arrival time < 0.2ms

            long size_IAT = listIAT1.size();
            for (double timeStamp : listIAT1) {
                if (timeStamp < Utils.THRESHOLD_IAT) {
                    PKT_IAT_02 ++;
                }
            }
            //PKT_IAT là %số gói tin có paket Inter-Arrival Time < 0.02
            P_IAT = (PKT_IAT_02 * 1.0 + 1) / size_IAT;
            Set<String> list = statisticHttp();

            Parameter par = new Parameter(RATE_ICMP,PPF,P_IAT,PKT_SIZE_AVG,NUMBER_PACKET,0,number_dns_respone,list);

            //Xóa thông tin của 6s đầu
            listFlow1.clear();
            listIAT1.clear();

            return par;
        }
        return null;
    }

    public EntropyPar statisticUDP() throws IOException {
        EntropyPar parameterUDP = getParameter(listFlow1);
        return parameterUDP;
    }

    public List<ParameterTCP> statisticTCP() throws IOException {
        List<ParameterTCP> list = getParameterTCP(listFlow1);
        return list;
    }

    public Set<String> statisticHttp(){
        Map<IPLink,Integer> map = new HashMap<>();
        if(listFlow1.size() != 0){
            for(Item item : listFlow1){
                String IPsrc = (String)item.getFieldValue(Flow.IP_SRC.toString());
                String link = (String)item.getFieldValue(Flow.LINK.toString());
                IPLink ipLink = new IPLink(IPsrc,link);
                if (map.containsKey(ipLink)){
                    int count = map.get(ipLink);
                    map.put(ipLink,count+1);
                }else {
                    map.put(ipLink,1);

                }
            }
            Set<String> list = new HashSet<>();
            for (Map.Entry<IPLink,Integer> entry : map.entrySet()){
                if(entry.getValue() >= Utils.THRESHOLD_HTTP){
                    list.add(entry.getKey().getIP());
                }
            }
            return list;
        }
        return new HashSet<>();
    }

    public Par run() {
        if(listFlow1.size() != 0) {

            int ONE_PKT_ON_FLOW = 0;//số flow có 1 gói tin
            int PKT_IAT_02 = 0;// số packet có inter-arrival time < 0.2ms

            int numberFlow = listFlow1.size();
            int numberICMP = 0;
            int numberTCP = 0;

            //Flow nào có danh sách paket Inter-Arrival Time rỗng (kích thước = 0) thì flow đó có 1 gói tin
            for (Item item : listFlow1) {
                int packetPerFlow = (Integer) item.getFieldValue(Flow.COUNT.toString());
                String pro = (String) item.getFieldValue(Flow.PROTOCOL.toString());
                if(packetPerFlow == 1){
                    ONE_PKT_ON_FLOW += 1;
                }
                if(pro.equals("ICMP")){
                    numberICMP += packetPerFlow;
                }
                if(pro.equals("TCP")){
                    numberTCP += packetPerFlow;
                }
            }

            //ONE_PKT_FLOW là %số flow có 1 gói tin trên tổng số flow
            double PPF = ONE_PKT_ON_FLOW * 1.0 / numberFlow;
            String maxRatePro = "";

//            System.out.println(numberICMP + " "+numberTCP);

            if(numberICMP > numberTCP){
                maxRatePro = "ICMP";
            }else {
                maxRatePro = "TCP";
            }

            long size_IAT = listIAT1.size();
            for (double timeStamp : listIAT1) {
                if (timeStamp < 0.0002) {
                    PKT_IAT_02 += 1;
                }
            }
            //PKT_IAT là %số gói tin có paket Inter-Arrival Time < 0.02
            double P_IAT = (PKT_IAT_02 * 1.0 - 1) / size_IAT;

            return new Par(P_IAT,PPF,maxRatePro);
        }
        return null;
    }

    private List<ParameterTCP> getParameterTCP(List<Item> items){
        List<ParameterTCP> list = new ArrayList<>();
        Map<String,Map<String,Long>> map = new HashMap<>();

        for(Item item : items) {

            long count = (Integer)item.getFieldValue(Flow.COUNT.toString());
            String ip_s = (String)item.getFieldValue(Flow.IP_SRC.toString());
            String port_s = (String)item.getFieldValue(Flow.PORT_SRC.toString());

            Map<String,Long> port_src = new HashMap<>();
            if(map.get(ip_s) == null){
                port_src.put(port_s,count);
                map.put(ip_s,port_src);
            }else {
                port_src = map.get(ip_s);
                port_src.put(port_s,count);
                map.put(ip_s,port_src);
            }
        }
        for (Map.Entry<String,Map<String,Long>> entry : map.entrySet()){
            ParameterTCP parameterTCP = new ParameterTCP(entry.getKey(),entry.getValue().size(),entropy(entry.getValue()));
            list.add(parameterTCP);
        }
        return list;
    }

    private EntropyPar getParameter(List<Item> items){
        Map<String,Long> ip_src = new HashMap<>();
        Map<String,Long> port_src = new HashMap<>();
        Map<String,Long> port_dst = new HashMap<>();
        Map<String,Long> protocol = new HashMap<>();
        long total = 0;
        for(Item item : items) {

            long count = (Integer)item.getFieldValue(Flow.COUNT.toString());
            total += count;
            String ip_s = (String)item.getFieldValue(Flow.IP_SRC.toString());
            String port_s = (String)item.getFieldValue(Flow.PORT_SRC.toString());
            String port_d = (String)item.getFieldValue(Flow.PORT_DST.toString());
            String proto = (String)item.getFieldValue(Flow.PROTOCOL.toString());
            if(ip_src.get(ip_s) == null){
                ip_src.put(ip_s,count);
            }else ip_src.put(ip_s,ip_src.get(ip_s)+count);

            if(port_src.get(port_s) == null){
                port_src.put(port_s,count);
            }else port_src.put(port_s,port_src.get(port_s)+count);

            if(port_dst.get(port_d) == null){
                port_dst.put(port_d,count);
            }else port_dst.put(port_d,port_dst.get(port_d)+count);

            if(protocol.get(proto) == null){
                protocol.put(proto,count);
            }else protocol.put(proto,protocol.get(proto)+count);
        }
        EntropyPar parameter = new EntropyPar(entropy(ip_src),entropy(port_src),entropy(port_dst),entropy(protocol),total);
        return parameter;
    }

    private double entropy(Map<? extends Object,Long> map){
        long sum = 0;
        double entro = 0;
        for(Map.Entry<? extends Object,Long> entry : map.entrySet()){
            sum += entry.getValue();
        }
        for(Map.Entry<? extends Object,Long> entry : map.entrySet()){
            double p = entry.getValue()*1.0/sum;

            entro += -p*Math.log(p)/Math.log(2);
        }
        return entro;
    }
}