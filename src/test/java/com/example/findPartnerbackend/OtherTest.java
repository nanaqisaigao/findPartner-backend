package com.example.findPartnerbackend;

import org.junit.jupiter.api.Test;

public class OtherTest {
    @Test
    public void leetcode5(){
        String s = "abadcab";
            if(s.equals("")){
                System.out.println("无结果");
            }
            String ori = s;
            String rev = new StringBuffer(s).reverse().toString();
            int len1 = ori.length();
            int [][]a = new int[len1+1][len1+1];
            int i,j;
            for (i = 0; i <= len1; i++) {
                for (j = 0; j <= len1; j++) {
                    a[i][j] = 0;
                }
            }

            for(i=1;i<len1+1;i++){
                for(j=1;j<len1+1;j++){
                    if(ori.charAt(i-1)==rev.charAt(j-1)){
                        a[i][j]=a[i-1][j-1]+1;
                    }
                    else{
                        a[i][j] = Math.max(a[i - 1][j], a[i][j - 1]);
                    }
                }
            }
            i=len1;
            j=len1;
            int L = a[i][j];
            StringBuffer result = new StringBuffer("");
            while(L>0){
                if(a[i][j]==a[i-1][j])
                    i--;
                else if(a[i][j]==a[i][j-1])
                    j--;
                else{
                    result.append(ori.charAt(i-1));
                    L--;
                    i--;
                    j--;
                }
            }
        System.out.println(result.reverse().toString());
        }
    }

