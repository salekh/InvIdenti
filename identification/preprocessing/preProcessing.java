package preprocessing;

import Base.patent;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.preprocessing.LanguageModelStemmer;
import org.carrot2.text.preprocessing.PreprocessingContext;

//import org.carrot2.text.preprocessing.Tokenizer;
import org.carrot2.text.preprocessing.pipeline.*;
import org.carrot2.text.preprocessing.filter.*;


import java.util.ArrayList;

/**
 * Created by sunlei on 15/8/21.
 */
public class preProcessing
{
    //String str="RNA is transcribed from a double stranded DNA template by forming a complex by hybridizing to the template at a desired transcription initiation site one or more oligonucleic acid analogues of the PNA type capable of forming a transcription initiation site with the DNA and exposing the complex to the action of a DNA dependant RNA polymerase in the presence of nucleoside triphosphates. Equal length transcripts may be obtained by placing a block to transcription downstream from the initiation site or by cutting the template at such a selected location. The initiation site is formed by displacement of one strand of the DNA locally by the PNA hybridization.";
    ArrayList<Document> docs=new ArrayList<Document>();
    LanguageCode language;
    PreprocessingContext context;
    ArrayList<patent> patents;


    public preProcessing(ArrayList<Document> docs)
    {
        this.docs=docs;
        completePreprocess();
    }

    private void completePreprocess()
    {
        context=new CompletePreprocessingPipeline().preprocess(docs,null,LanguageCode.ENGLISH);
        System.out.println(context.allWords);
    }

    public String removeStopWords()
    {
        ArrayList<String> strs=new ArrayList<String>();
        String s_r="";
        System.out.println(this.context.allLabels.featureIndex.length);
        for(int i=0;i<this.context.allLabels.featureIndex.length;i++)
        {
            if(this.context.allLabels.featureIndex[i]<this.context.allWords.tf.length)
            {
                s_r+=this.context.allWords.image[i];
                s_r+=" ";
            }
        }
        return s_r;
    }


    public static void main(String[] args)
    {
        String str="RNA is transcribed from a double stranded DNA template by forming a complex by hybridizing to the template at a desired transcription initiation site one or more oligonucleic acid analogues of the PNA type capable of forming a transcription initiation site with the DNA and exposing the complex to the action of a DNA dependant RNA polymerase in the presence of nucleoside triphosphates. Equal length transcripts may be obtained by placing a block to transcription downstream from the initiation site or by cutting the template at such a selected location. The initiation site is formed by displacement of one strand of the DNA locally by the PNA hybridization.";
        ArrayList<Document> docs=new ArrayList<Document>();
        docs.add(new Document(str));
        /*
        PreprocessingContext c=new CompletePreprocessingPipeline().preprocess(docs,null,LanguageCode.ENGLISH);
       // System.out.println(c);
        StopWordLabelFilter f=new StopWordLabelFilter();
        PreprocessingContext.AllWords words=c.allWords;

        System.out.println(c);
        boolean[] accpetwords=new boolean[c.allStems.tf.length];
        boolean[] accpetphrase=new boolean[c.allPhrases.tf.length];

        //System.out.println(c.allWords.fieldIndices.length);
        for(int i=0;i<c.allStems.tf.length;i++)
        {
            if (f.acceptWord(c,i)) accpetwords[i]=true; else accpetwords[i]=false;

        }
        for(int i=0;i<c.allPhrases.tf.length;i++)
        {

            if (f.acceptPhrase(c,i)) accpetphrase[i]=true; else accpetphrase[i]=false;
        }
        new CompleteLabelFilter().filter(c,accpetwords,accpetphrase);
        System.out.println(c);
        //LanguageModel.create();
        //PreprocessingContext c=new PreprocessingContext(LanguageModel.create(),docs,null);
        */
        preProcessing p=new preProcessing(docs);

        System.out.println(p.removeStopWords());
    }
}
