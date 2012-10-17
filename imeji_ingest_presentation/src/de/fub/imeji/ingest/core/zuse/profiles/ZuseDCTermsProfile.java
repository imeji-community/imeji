/**
 * 
 */
package de.fub.imeji.ingest.core.zuse.profiles;


import java.util.ArrayList;
import java.util.Arrays;

import de.fub.imeji.ingest.core.zuse.beans.ZuseDCTermsBean;
import de.fub.imeji.ingest.core.zuse.metadata.terms.ZuseDCTerms;


/**
 * @author hnguyen
 *
 */
public class ZuseDCTermsProfile {
		
	public final static String METADATA_ALT_SIGNATUR = "Alt-Signatur";
	public final static String METADATA_ALT_TITLE = "Alt-Titel";
	public final static String METADATA_BAUTEIL_NR = "Bauteil-Nr_";
	public final static String METADATA_BEMERKUNG = "Bemerkung";
	public final static String METADATA_BESCHREIBSTOFF = "Beschreibstoff";
	public final static String METADATA_BESTAND = "Bestand";
	public final static String METADATA_BILDFORMAT_HXB_CM = "Bildformat_-hxb_cm-";
	public final static String METADATA_BILDSTELLEN_NEGATIV = "Bildstellen-Negativ";
	public final static String METADATA_BILDSTELLEN_NR = "Bildstellen-Nr_";
	public final static String METADATA_BLOCK_NR = "Block-Nr_";
	public final static String METADATA_DARSTELLUNG = "Darstellung";
	public final static String METADATA_DATEINAME = "Dateiname";
	public final static String METADATA_DATIERUNG = "Datierung";
	public final static String METADATA_DIA_NR = "Dia-Nr_";
	public final static String METADATA_DISCHINGER_TRANSKRIPTION = "Dischinger-Transkription";
	public final static String METADATA_ENTHAELT = "Enthält";
	public final static String METADATA_ERHALTUNGSZUSTANG = "Erhaltungszustand";
	public final static String METADATA_FORMAT_BXH_CM = "Format_-bxh_cm-";
	public final static String METADATA_GEGENSTAND = "Gegenstand";
	public final static String METADATA_GMD_DATEN = "GMD-DATEN";
	public final static String METADATA_GMD_NR = "GMD-Nr_";
	public final static String METADATA_KOERPERSCHAFT = "Körperschaft";
	public final static String METADATA_LAUFZEIT = "Laufzeit";
	public final static String METADATA_MATERIALKLASSIFIKATION = "Materialklassifikation";
	public final static String METADATA_MASSSTAB = "Maßstab";
	public final static String METADATA_MEHRFACHEXEMPLAR = "Mehrfachexemplar";
	public final static String METADATA_ORT = "Ort";
	public final static String METADATA_PND_NR_VERFASSER = "PND-Nr__Verfasser";
	public final static String METADATA_PERSON = "Person";
	public final static String METADATA_SIGNATUR = "Signatur";
	public final static String METADATA_SCHLAGWORT = "Schlagwort";
	public final static String METADATA_STENO_SEITENUMFANG = "Steno-Seitenumfang";
	public final static String METADATA_STENOGRAMM = "Stenogramm";
	public final static String METADATA_SYO = "Syo";
	public final static String METADATA_SYS = "Sys";
	public final static String METADATA_SYS_2 = "Sys2";
	public final static String METADATA_SYS_3 = "Sys3";
	public final static String METADATA_TECHNIK = "Technik";
	public final static String METADATA_TITEL = "Titel";
	public final static String METADATA_VERFASSER = "Verfasser";
	public final static String METADATA_UMFANG = "Umfang";
	public final static String METADATA_VORL_DIA_NR = "vorl__Dia-Nr_";
	public final static String METADATA_VORL_NR = "Vorl__Nr_";
	public final static String METADATA_ZUSE_FOTO_NR = "Zuse_Foto-Nr_";
	public final static String METADATA_ZUSE_WEITERE_FOTO_NR = "Zuse_weitere_Foto-Nr_";
	public final static String METADATA_ZEICHNER_HERSTELLER = "Zeichner-Hersteller";
	
	
	private static ArrayList<ArrayList<ZuseDCTermsBean>> zuseProfiles = null;
	
	/**
	 * 
	 * @param amount
	 * @return
	 */
	public static ArrayList<ArrayList<ZuseDCTermsBean>> getDCTermsProfiles(int amount) {
		if(zuseProfiles == null) {
			zuseProfiles = new ArrayList<ArrayList<ZuseDCTermsBean>>(amount);
			for (int j = 0; j < amount; j++) {
				int i = 1;
				ArrayList<ZuseDCTermsBean> zuseProfile = new ArrayList<ZuseDCTermsBean>();
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ALT_SIGNATUR)),ZuseDCTerms.ALTERNATIVE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ALT_TITLE)),ZuseDCTerms.ALTERNATIVE(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BAUTEIL_NR)),ZuseDCTerms.DESCRIPTION(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BEMERKUNG)),ZuseDCTerms.DESCRIPTION(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BESCHREIBSTOFF)),ZuseDCTerms.FORMAT(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BESTAND)),ZuseDCTerms.IDENTIFIERCOLLECTION(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BILDFORMAT_HXB_CM)),ZuseDCTerms.EXTENTPHYSICALMEDIUM(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BILDSTELLEN_NEGATIV)),ZuseDCTerms.SOURCE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BILDSTELLEN_NR)),ZuseDCTerms.SOURCE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_BLOCK_NR)),ZuseDCTerms.DESCRIPTION(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_DARSTELLUNG)),ZuseDCTerms.FORMAT(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_DATEINAME)), ZuseDCTerms.DESCRIPTION(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_DATIERUNG)),ZuseDCTerms.CREATED(i++, true, "de")));				
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_DIA_NR)),ZuseDCTerms.SOURCE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_DISCHINGER_TRANSKRIPTION)),ZuseDCTerms.HASVERSION(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ENTHAELT)),ZuseDCTerms.SUBJECT(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ERHALTUNGSZUSTANG)),ZuseDCTerms.FORMAT(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_FORMAT_BXH_CM)),ZuseDCTerms.EXTENTPHYSICALMEDIUM(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_GEGENSTAND)),ZuseDCTerms.FORMAT(i++, true, null)));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_GMD_DATEN,METADATA_GMD_NR)),ZuseDCTerms.IDENTIFIERDATASET(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_KOERPERSCHAFT)),ZuseDCTerms.CONTRIBUTOR(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_LAUFZEIT)),ZuseDCTerms.CREATED(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_MATERIALKLASSIFIKATION)),ZuseDCTerms.FORMAT(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_MASSSTAB)),ZuseDCTerms.FORMAT(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_MEHRFACHEXEMPLAR)),ZuseDCTerms.HASFORMAT(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ORT)),ZuseDCTerms.SPATIAL(i++, true, "de")));				
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_PND_NR_VERFASSER)),ZuseDCTerms.IDENTIFIERDATASET(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_PERSON)),ZuseDCTerms.CONTRIBUTOR(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_SIGNATUR)),ZuseDCTerms.IDENTIFIERDATASET(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_SCHLAGWORT)),ZuseDCTerms.SUBJECT(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_STENO_SEITENUMFANG)),ZuseDCTerms.EXTENT(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_STENOGRAMM)),ZuseDCTerms.FORMAT(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_SYO)),ZuseDCTerms.TYPE(i++, true, null)));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_SYS)),ZuseDCTerms.TYPE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_SYS_2)),ZuseDCTerms.TYPE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_SYS_3)),ZuseDCTerms.TYPE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_TECHNIK)),ZuseDCTerms.FORMAT(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_TITEL)),ZuseDCTerms.TITLE(i++, false, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_VERFASSER)),ZuseDCTerms.CREATOR(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_UMFANG)),ZuseDCTerms.EXTENTSIZEORDURATION(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_VORL_DIA_NR)),ZuseDCTerms.SOURCE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_VORL_NR)),ZuseDCTerms.SOURCE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ZUSE_FOTO_NR)),ZuseDCTerms.SOURCE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ZUSE_WEITERE_FOTO_NR)),ZuseDCTerms.SOURCE(i++, true, "de")));
				zuseProfile.add(new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(METADATA_ZEICHNER_HERSTELLER)), ZuseDCTerms.CREATOR(i++, true, "de")));
				zuseProfiles.add(zuseProfile);
			}
		}
		
		return zuseProfiles;
	}
	
}
