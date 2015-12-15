package com.meo;

/**
 * Created by Ilya Evlampiev on 14.12.2015.
 */
public class MeOServerTestUtility {
    public static void main(String[] args)
    {

        try {
            MeOServer.main(new String[]{});
            Thread.sleep(10000);
            MeOServer.globalMessageList.add(new Message("Test from utility text","79047640086"));
            MeOServer.globalMessageList.add(new Message("Test from utility text2","79047640086"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
