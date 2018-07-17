package cn.edu.sdu.online.isdu.bean;

/**
 * @author Cola_Mentos
 * @date 2018/7/11
 */

public class Grade {

    /**
     * pscj:平时成绩
     * qmcj:期末成绩
     * cj:成绩
     * jd:绩点
     * zgf:班级最高分
     * zdf:班级最低分
     * pm:排名
     * zrs:总人数
     * dd:等第
     */

    private double pscj, qmcj, cj, jd, zgf, zdf;
    private int pm, zrs;
    private String dd;

    public Grade() {
    }

    public void setPscj(double pscj) {
        this.pscj = pscj;
    }

    public void setQmcj(double qmcj) {
        this.qmcj = qmcj;
    }

    public void setCj(double cj) {
        this.cj = cj;
    }

    public void setJd(double jd) {
        this.jd = jd;
    }

    public void setZgf(double zgf) {
        this.zgf = zgf;
    }

    public void setZdf(double zdf) {
        this.zdf = zdf;
    }

    public void setPm(int pm) {
        this.pm = pm;
    }

    public void setZrs(int zrs) {
        this.zrs = zrs;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public double getPscj() {
        return pscj;
    }

    public double getQmcj() {
        return qmcj;
    }

    public double getCj() {
        return cj;
    }

    public double getJd() {
        return jd;
    }

    public double getZdf() {
        return zdf;
    }

    public double getZgf() {
        return zgf;
    }

    public int getPm() {
        return pm;
    }

    public int getZrs() {
        return zrs;
    }

    public String getDd() {
        return dd;
    }

}
