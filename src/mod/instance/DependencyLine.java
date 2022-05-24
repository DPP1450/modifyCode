package mod.instance;

import java.awt.*;

import javax.swing.JPanel;

import Define.AreaDefine;
import Pack.DragPack;
import bgWork.handler.CanvasPanelHandler;
import mod.IFuncComponent;
import mod.ILinePainter;
import java.lang.Math;

public class DependencyLine extends JPanel
        implements IFuncComponent, ILinePainter {
    JPanel from;
    int fromSide;
    Point fp = new Point(0, 0);
    JPanel to;
    int toSide;
    Point tp = new Point(0, 0);
    int arrowSize = 6;
    int panelExtendSize = 10;
    boolean isSelect = false;
    int selectBoxSize = 5;
    CanvasPanelHandler cph;
    boolean isHightlight = false;

    public DependencyLine(CanvasPanelHandler cph) {
        this.setOpaque(false);
        this.setVisible(true);
        this.setMinimumSize(new Dimension(1, 1));
        this.cph = cph;
    }

    @Override
    public void paintComponent(Graphics g) {
        Point fpPrime;
        Point tpPrime;
        renewConnect();
        fpPrime = new Point(fp.x - this.getLocation().x,
                fp.y - this.getLocation().y);
        tpPrime = new Point(tp.x - this.getLocation().x,
                tp.y - this.getLocation().y);
        drawDashedALine(g, fpPrime.x, fpPrime.y, tpPrime.x, tpPrime.y);
        if (isSelect == true) {
            paintSelect(g);
        }
    }

    @Override
    public void reSize() {
        Dimension size = new Dimension(
                Math.abs(fp.x - tp.x) + panelExtendSize * 2,
                Math.abs(fp.y - tp.y) + panelExtendSize * 2);
        this.setSize(size);
        this.setLocation(Math.min(fp.x, tp.x) - panelExtendSize,
                Math.min(fp.y, tp.y) - panelExtendSize);
    }

    @Override
    public void paintArrow(Graphics g, Point point) {

    }

    @Override
    public void setConnect(DragPack dPack) {
        Point mfp = dPack.getFrom();
        Point mtp = dPack.getTo();
        from = (JPanel) dPack.getFromObj();
        to = (JPanel) dPack.getToObj();
        fromSide = new AreaDefine().getArea(from.getLocation(), from.getSize(),
                mfp);
        toSide = new AreaDefine().getArea(to.getLocation(), to.getSize(), mtp);
        renewConnect();
        System.out.println("from side " + fromSide);
        System.out.println("to side " + toSide);
        ;
    }

    void renewConnect() {
        try {
            fp = getConnectPoint(from, fromSide);
            tp = getConnectPoint(to, toSide);
            this.reSize();
        } catch (NullPointerException e) {
            this.setVisible(false);
            cph.removeComponent(this);
        }
    }

    Point getConnectPoint(JPanel jp, int side) {
        Point temp = new Point(0, 0);
        Point jpLocation = cph.getAbsLocation(jp);
        if (side == new AreaDefine().TOP) {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
            temp.y = jpLocation.y;
        } else if (side == new AreaDefine().RIGHT) {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth());
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
        } else if (side == new AreaDefine().LEFT) {
            temp.x = jpLocation.x;
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
        } else if (side == new AreaDefine().BOTTOM) {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight());
        } else {
            temp = null;
            System.err.println("getConnectPoint fail:" + side);
        }
        return temp;
    }

    @Override
    public void paintSelect(Graphics gra) {
        gra.fillRect(fp.x, fp.y, selectBoxSize, selectBoxSize);
        gra.fillRect(tp.x, tp.y, selectBoxSize, selectBoxSize);
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public void drawDashedALine(Graphics g, double sx, double sy, double ex, double ey) {
        Graphics2D g2 = (Graphics2D) g;
        double H = 10, L = 10;
        double awrad = Math.atan(L / H);
        double arraow_len = Math.sqrt(L * L + H * H);
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0];
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0];
        double y_4 = ey - arrXY_2[1];
        if ((from == cph.selectedJpanel && fromSide == cph.selectedJpanelSide) || (to == cph.selectedJpanel
                && toSide == cph.selectedJpanelSide))
            g2.setColor(Color.RED);
        else
            g2.setColor(Color.BLACK);
        g2.drawLine((int) ex, (int) ey, (int) x_3, (int) y_3);
        g2.drawLine((int) ex, (int) ey, (int) x_4, (int) y_4);
        drawDashedLine(g2, (int) sx, (int) sy, (int) ex, (int) ey);

    }

    public void drawDashedLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[] { 9 }, 0);
        if ((from == cph.selectedJpanel && fromSide == cph.selectedJpanelSide) || (to == cph.selectedJpanel
                && toSide == cph.selectedJpanelSide))
            g2.setColor(Color.RED);
        else
            g2.setColor(Color.BLACK);
        g2.setStroke(dashed);
        g2.drawLine(x1, y1, x2, y2);
        g2.dispose();
    }

    public static double[] rotateVec(double e, double f, double ang,
            boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        double vx = e * Math.cos(ang) - f * Math.sin(ang);
        double vy = e * Math.sin(ang) + f * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
}
