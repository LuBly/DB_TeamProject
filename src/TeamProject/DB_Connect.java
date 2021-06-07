package TeamProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

public class DB_Connect {

    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://localhost/teamproject";
        String username = "lumenize";
        String password = "audi8621";
        String[] area = {"서울","부산","대구","인천","대전","광주","울산","강원","경기","충청남도","충청북도","전라북도","전라남도","경상북도","경상남도","제주도"};

        String[] empmSttsCn = new String[200];
        int empm_count = 0;

        String[] accrRqisCn = new String[200];
        int accr_count = 0;

        String[] majrRqisCn = new String[200];
        int majr_count = 0;

        String[] splzRlmRqisCn = new String[200];
        int splz_count = 0;
        try {
            Scanner scan = new Scanner(System.in);

            //DB연결
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            //DB
            Statement nowState = connection.createStatement();
            System.out.println("connection");

            //DB에 저장된 테이블이 있다면 지우고 없다면 넘어가기
            String DropTable = "DROP TABLE IF EXISTS UserInfo CASCADE; DROP TABLE IF EXISTS PolicyInfo CASCADE; DROP TABLE IF EXISTS CountInfo CASCADE;";
			nowState.execute(DropTable);
            //DB 생성

            //empmSttsCn : 취업상태 , accrRqisCn : 학력 , majrRqisCn : 전공 , splzRlmRqisCn : 특화분야
            //bizId : 정책 번호 ,polyBizSjnm : 정책명, polyItcnCn : 정책소개, plcyTpNm : 정책유형, rqutPrdCn : 신청기간 , rqutUrla : 사이트 주소, polyBizTy : 기관 및 지자채 구분, AgeInfo : 연령, empmSttsCn : 취업상태, accrRqisCn : 학력,  majrRqisCn : 전공, splzRlmRqisCn : 특화분야
            //bizId : 정책 번호, cnt : 검색횟수

            String CreateUserInfo = "Create Table UserInfo(userID int,userPassword int, area varchar(20), ageInfo int, empmSttsCn varchar(20),accrRqisCn varchar(20), majrRqisCn varchar(20), splzRlmRqisCn varchar(20));";
            String CreatePolicyInfo = "Create Table PolicyInfo(bizId varchar(100),polyBizTy varchar(100),polyBizSjnm varchar(100),polyItcnCn varchar(200),plcyTpNm varchar(100),ageInfo varchar(100),empmSttsCn varchar(100),accrRqisCn varchar(100),majrRqisCn varchar(100), splzRlmRqisCn varchar(100), rqutPrdCn varchar(300), rqutUrla varchar(200));";
            String CreateCountInfo = "Create Table CountInfo(bizld varchar(20),cnt int);";

            nowState.execute(CreateUserInfo);
            nowState.execute(CreatePolicyInfo);
            nowState.execute(CreateCountInfo);

            String[][] Data = DB_Split.main();




            for(int i = 1; i < 201; i++) {
                System.out.println("\n정책 " + i + "\n");
                String insertSQL_PolicyInfo = "insert into PolicyInfo values(";
                for(int j = 0; j < 12; j++) {
                    System.out.println(Data[i][j].length());//input str의 길이를 check하기 위함.
                    insertSQL_PolicyInfo += "'"+ Data[i][j]+"',";
                }
                insertSQL_PolicyInfo = insertSQL_PolicyInfo.replaceFirst(".$","");//마지막 , 제거
                insertSQL_PolicyInfo += ");";
                System.out.println(insertSQL_PolicyInfo);
                nowState.execute(insertSQL_PolicyInfo);

                if(Data[i][6].compareTo("제한없음")!=0){
                    empmSttsCn[empm_count]=Data[i][6];
                    empm_count ++;
                }
                if(Data[i][7].compareTo("제한없음")!=0){
                    accrRqisCn[accr_count]=Data[i][7];
                    accr_count ++;
                }
                if(Data[i][8].compareTo("제한없음")!=0){
                    majrRqisCn[majr_count]=Data[i][8];
                    majr_count ++;
                }
                if(Data[i][9].compareTo("제한없음")!=0){
                    splzRlmRqisCn[splz_count]=Data[i][9];
                    splz_count ++;
                }

                System.out.println("insert "+i);
                System.out.println("\n------------------------------------------");
            }

            for(int i = 1; i < 201; i++) {
                Random random = new Random();
                System.out.println("\n유저 " + i + "\n");

                String insertSQL_UserInfo = "insert into UserInfo values('"+i+"','"+i+"','"
                        +area[random.nextInt(area.length)]//지역
                        +"','"+(random.nextInt(60)+10)+"','"//나이 10~70
                        +empmSttsCn[random.nextInt(empm_count)]+"','"//취업상태
                        +accrRqisCn[random.nextInt(accr_count)]+"','"//학력
                        +majrRqisCn[random.nextInt(1)]+"','"//전공_직접입력_>>생략 건의<<
                        +splzRlmRqisCn[random.nextInt(splz_count)]+"');";//특화분야
                System.out.println(insertSQL_UserInfo);
                //nowState.execute(insertSQL_UserInfo);
                System.out.println("insert "+i);
                System.out.println("\n------------------------------------------");
            }


        } catch(SQLException e) {

        }
    }
}
