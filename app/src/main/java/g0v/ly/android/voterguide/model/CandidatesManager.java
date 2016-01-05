package g0v.ly.android.voterguide.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import g0v.ly.android.voterguide.net.WebRequest;

public class CandidatesManager {
    private static final Logger logger = LoggerFactory.getLogger(CandidatesManager.class);

    private static CandidatesManager instance;

    private List<Candidate> allCandidates = new ArrayList<>();

    private CandidatesManager() {}

    public static CandidatesManager getInstance() {
        synchronized (CandidatesManager.class) {
            if (instance == null) {
                instance = new CandidatesManager();
            }
        }
        return instance;
    }

    /**
     * Blocking method
     * @param county
     * @return Candidate list
     */
    public List<Candidate> getCandidatesWithCounty(String county) {
        boolean hasDownloadBefore = false;
        List<Candidate> candidates = new ArrayList<>();

        for (Candidate candidate : allCandidates) {
            if (candidate.county.equals(county)) {
                hasDownloadBefore = true;
                break;
            }
        }

        if (hasDownloadBefore) {
            for (Candidate candidate : allCandidates) {
                if (candidate.county.equals(county)) {
                    candidates.add(candidate);
                }
            }
        }
        else {
            candidates = downloadCandidatesOfCounty(county);
            allCandidates.addAll(candidates);
        }

        return candidates;
    }

    public Candidate getCandidateWithName(String name) {
        for (Candidate tempCandidate : allCandidates) {
            if (tempCandidate.name.equals(name)) {
                return tempCandidate;
            }
        }

        return null;
    }

    private List<Candidate> downloadCandidatesOfCounty(final String countyString) {
        List<Candidate> candidates = new ArrayList<>();
        String countryStringInEnglish = "";
        try {
            countryStringInEnglish = URLEncoder.encode(countyString, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            logger.debug(e.getMessage());
        }

        String rawResultString = WebRequest.create()
                .sendHttpRequestForResponse(WebRequest.G0V_LY_VOTE_API_URL, "ad=9&county=" + countryStringInEnglish);
        try {
            JSONObject rawObject = new JSONObject(rawResultString);

            JSONArray candidatesArray = rawObject.getJSONArray("results");

            for (int i = 0; i < candidatesArray.length(); i++) {
                JSONObject candidateObject = candidatesArray.getJSONObject(i);
                Candidate candidate = new Candidate(candidateObject);
                candidates.add(candidate);
            }
        }
        catch (JSONException e) {
            logger.debug(e.getMessage());
        }

        return candidates;
    }
}