package TeamProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DB_Connect {

    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://localhost/teamproject";
        String username = "lumenize";
        String password = "audi8621";
        try {
            Scanner scan = new Scanner(System.in);

            //DB연결
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            //DB
            Statement nowState = connection.createStatement();
            System.out.println("connection");

            //DB에 저장된 테이블이 있다면 지우고 없다면 넘어가기
            String DropTable = "DROP TABLE IF EXISTS UserInfo CASCADE; DROP TABLE IF EXISTS PolicyInfo CASCADE; DROP TABLE IF EXISTS CountInfo CASCADE;";
			//nowState.executeUpdate(DropTable);

            //DB 생성

            //empmSttsCn : 취업상태 , accrRqisCn : 학력 , majrRqisCn : 전공 , splzRlmRqisCn : 특화분야
            //bizId : 정책 번호 ,polyBizSjnm : 정책명, polyItcnCn : 정책소개, plcyTpNm : 정책유형, rqutPrdCn : 신청기간 , rqutUrla : 주소, polyBizTy : 기관 및 지자채 구분, AgeInfo : 연령, empmSttsCn : 취업상태, accrRqisCn : 학력,  majrRqisCn : 전공, splzRlmRqisCn : 특화분야
            //bizId : 정책 번호, cnt : 검색횟수

            String CreateUserInfo = "Create Table UserInfo(userID text(),userPassword text(), area text(), ageInfo int, empmSttsCn text(),accrRqisCn text(), majrRqisCn text(), splzRlmRqisCn text())";
            String CreatePolicyInfo = "Create Table PolicyInfo(bizId text(),polyBizSjnm text(),polyItcnCn text(),plcyTpNm text(),rqutPrdCn text(),rqutUrla text(),polyBizTy text(),AgeInfo text(),empmSttsCn text(), accrRqisCn text(), majrRqisCn text(), splzRlmRqisCn text())";
            String CreateCountInfo = "Create Table CountInfo(bizld text(),cnt int);";

            //nowState.executeUpdate(CreateUserInfo);
            //nowState.executeUpdate(CreatePolicyInfo);
            //nowState.executeUpdate(CreateCountInfo);


            String[][] Data = DB_Split.main();

            for(int i = 1; i < 201; i++) {
                System.out.println("\n정책 " + i + "\n");
                for(int j = 0; j < 12; j++) {
                    System.out.println(Data[i][j]);
                }
                System.out.println("\n------------------------------------------");
            }



        } catch(SQLException e) {

        }
    }
}
