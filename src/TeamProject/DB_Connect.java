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
        String[] accrRqisCn = {"고등학생","고졸","대학생","대학생(휴학)","대학 졸업"};
        String[] majrRqisCn = {"기계공학과","산업공학과","화학공학과","신소재공학과","응용화학생명공학과","환경안전공학과","건설시스템공학과","교통시스템공학과","건축학과","융합시스템공학과"
                                ,"전자공학과","소프트웨어학과","사이버보안학과","미디어학과","국방디지털융합학과","인공지능융합학과"
                                ,"수학과","물리학과","화학과","생명과학과"
                                ,"경영학과","e-비지니스학과","금융공학과","글로벌경영학과"
                                ,"국어국문학과","사학과","영어영문학과","문화콘텐츠학과","불어불문학과"
                                ,"경제학과","사회학과","행정학과","심리학과","정치외교학과","스포츠레저학과"
                                ,"의학과","간호학과","약학과"};

        String[] empmSttsCn = new String[200];
        int empm_count = 0;

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

            String CreateUserInfo = "Create Table UserInfo(userID int,userPassword int, area varchar(20), ageInfo int, empmSttsCn varchar(20),accrRqisCn varchar(20), majrRqisCn varchar(20), splzRlmRqisCn varchar(100));";
            String CreatePolicyInfo = "Create Table PolicyInfo(bizId varchar(100),polyBizTy varchar(100),polyBizSjnm varchar(100),polyItcnCn varchar(200),plcyTpNm varchar(100),ageInfo varchar(100), minAge int, maxAge int, empmSttsCn varchar(100),accrRqisCn varchar(100),majrRqisCn varchar(100), splzRlmRqisCn varchar(100), rqutPrdCn varchar(300), rqutUrla varchar(200));";
            String CreateCountInfo = "Create Table CountInfo(bizld varchar(20),cnt int);";

            nowState.execute(CreateUserInfo);
            nowState.execute(CreatePolicyInfo);
            nowState.execute(CreateCountInfo);

            String[][] Data = DB_Split.main();




            for(int i = 1; i < 201; i++) {
                System.out.println("\n정책 " + i + "\n");
                String insertSQL_PolicyInfo = "insert into PolicyInfo values(";
                String insertSQL_CountInfo = "insert into CountInfo values(";
                for(int j = 0; j < 6; j++) {
                    insertSQL_PolicyInfo += "'"+ Data[i][j]+"',";
                }

                if(Data[i][5].compareTo("제한없음")==0){//제한없음일 때 min=0, max=99
                    insertSQL_PolicyInfo += "'0','99',";
                }
                else{
                    String Age_num = Data[i][5].replaceAll("[^\\d]","");//나이 영역에서 숫자만 추출 경우1. 만 19세 ~ 34세, 경우2 만18세 이상
                    System.out.println(Age_num);
                    if(Age_num.length()>3){//경우1 1934
                        String minAge = Age_num.substring(0,2);
                        String maxAge = Age_num.substring(2);
                        insertSQL_PolicyInfo += "'"+minAge+"','"+maxAge+"',";
                    }
                    else if(Age_num.length()==3){
                        String minAge = Age_num.substring(0,1);
                        String maxAge = Age_num.substring(1);
                        insertSQL_PolicyInfo += "'"+minAge+"','"+maxAge+"',";
                    }
                    else{
                        insertSQL_PolicyInfo += "'"+Age_num+"','99',";
                    }
                }

                for(int j = 8; j < 14; j++) {
                    insertSQL_PolicyInfo += "'"+ Data[i][j-2]+"',";
                }

                insertSQL_PolicyInfo = insertSQL_PolicyInfo.replaceFirst(".$","");//마지막 , 제거
                insertSQL_PolicyInfo += ");";
                insertSQL_CountInfo += "'"+Data[i][0]+"',0);";

                System.out.println(insertSQL_PolicyInfo);
                System.out.println(insertSQL_CountInfo);

                nowState.execute(insertSQL_PolicyInfo);
                nowState.execute(insertSQL_CountInfo);

                if(Data[i][8].compareTo("제한없음")!=0){
                    empmSttsCn[empm_count]=Data[i][8];
                    empm_count ++;
                }

                if(Data[i][11].compareTo("제한없음")!=0){
                    splzRlmRqisCn[splz_count]=Data[i][11];
                    splz_count ++;
                }

                System.out.println("insert "+i);
                System.out.println("\n------------------------------------------");
            }
/*
            for(int i = 1; i < 201; i++) {
                Random random = new Random();
                System.out.println("\n유저 " + i + "\n");
                int empm_cur=random.nextInt(empm_count);
                if(empmSttsCn[empm_cur].length()>20){
                    empmSttsCn[empm_cur] = "자영업자";
                }

                String insertSQL_UserInfo = "insert into UserInfo values('"+i+"','"+i+"','"
                        +area[random.nextInt(area.length)]//지역
                        +"','"+(random.nextInt(60)+20)+"','"//나이 10~70
                        +empmSttsCn[empm_cur]+"','"//취업상태
                        +accrRqisCn[random.nextInt(accrRqisCn.length)]+"','"//학력
                        +majrRqisCn[random.nextInt(majrRqisCn.length)]+"','"//전공
                        +splzRlmRqisCn[random.nextInt(splz_count)]+"');";//특화분야
                System.out.println(insertSQL_UserInfo);
                nowState.execute(insertSQL_UserInfo);
                System.out.println("insert "+i);
                System.out.println("\n------------------------------------------");
            }
*/

        } catch(SQLException e) {

        }
    }
}
