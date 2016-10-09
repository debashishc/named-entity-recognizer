package nlp.opennlp;

/**
 * @author Debashish Chakraborty
 * @version 1.0
 */
public class TemplateRow {

    public String word;
    public String tag;
    public String chunk;
    public int freq;



    public TemplateRow(String word, String tag) {
        this.word = word;
        this.tag = tag;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }


}
