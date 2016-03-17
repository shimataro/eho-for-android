package com.example.shimataro.eho;

/**
 * Created by shimataro on 16/03/17.
 */
public class Eho {
    enum EHO {
        WSW, SSE, NNW, ENE,
    }

    private static final EHO   m_eho_symbols[] = {EHO.WSW, EHO.SSE, EHO.NNW, EHO.SSE, EHO.ENE};
    private static final float m_eho_orientations[] = {(float)247.5, (float)157.5, (float)337.5, (float)157.5, (float)67.5};
    private int m_year = 0;

    public void setYear(final int year) {
        m_year = year;
    }

    public EHO getSymbol() {
        return m_eho_symbols[m_year % 5];

    }

    public float getOrientation() {
        return m_eho_orientations[m_year % 5];
    }
}
