package app.as_service.util;


//https://thinkerodeng.tistory.com/324
public class CRCChecker {

    private boolean isCreatedCrcTable = false;
    private final short[] crcTable = new short[256];
    public short cnCrc16 = (short)0x8005;

    /**
     * checkSum
     * @param data 검증 데이터
     * @param checkCrc crc
     * @return true : 성공, false 실패
     */

    public boolean checkSum(byte[] data,  byte[] checkCrc) {
        short crc = crcUpdate(data, data.length);
        byte[] bCrc = fnShortToBytes(crc,1);

        return bCrc[0] == checkCrc[0] && bCrc[1] == checkCrc[1];
    }

    public boolean checkSum(String data, String checkCrc){
        return checkSum(data.getBytes(),checkCrc.getBytes());
    }

    /**
     * crc 테이블 생성
     * @param poly
     */
    private void crcGenerateTable(short poly) {
        short data;
        short accum;
        for(short i = 0; Short.toUnsignedInt(i) < 256; i++) {
            data = (short)(Short.toUnsignedInt(i) << 8);
            accum = 0;
            for(short j = 0; Short.toUnsignedInt(j) < 8; j++) {
                if(((Short.toUnsignedInt(data) ^ Short.toUnsignedInt(accum)) & 0x8000) != 0) {
                    accum = (short)(Short.toUnsignedInt(accum) << 1 ^ Short.toUnsignedInt(poly));
                } else {
                    accum = (short)(Short.toUnsignedInt(accum) << 1);
                }
                data = (short)(Short.toUnsignedInt(data) << 1);
            }

            crcTable[Short.toUnsignedInt(i)] = accum;
        }
        isCreatedCrcTable = true;
    }

    private short crcUpdate(byte[] data, int size) {
        int dataIndex = 0;
        short accum = 0;
        if (!isCreatedCrcTable) {
            crcGenerateTable(cnCrc16);
        }
        for(short i = 0; Short.toUnsignedInt(i) < size; i++) {
            accum = (short)(Short.toUnsignedInt(accum) << 8 ^ Short.toUnsignedInt(crcTable[Short.toUnsignedInt(accum) >> 8 ^ Byte.toUnsignedInt(data[dataIndex++])]));
        }
        return accum;
    }



    private byte[] fnShortToBytes(short value, int order){
        byte[] temp;
        temp = new byte[]{ (byte)((value & 0xFF00) >> 8), (byte)(value & 0x00FF) };
        temp = fnChangeByteOrder(temp,order);
        return temp;
    }

    private byte[] fnChangeByteOrder(byte[] value,int Order){
        int idx = value.length;
        byte[] Temp = new byte[idx];

        if(Order == 1){
            Temp = value;
        }else if(Order == 0){
            for(int i=0;i<idx;i++) {
                Temp[i] = value[idx-(i+1)];
            }
        }
        return Temp;
    }
}