package com.jstarcraft.nlp.lucene.ik;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;

/**
 * IK分词器 Lucene Tokenizer适配器类 兼容Lucene 4.0版本
 */
@SuppressWarnings("unused")
public final class IkTokenizer extends Tokenizer {

    // IK分词器实现
    private IKSegmenter _IKImplement;

    // 词元文本属性
    private CharTermAttribute termAtt;
    // 词元位移属性
    private OffsetAttribute offsetAtt;
    // 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
    private TypeAttribute typeAtt;
    // 记录最后一个词元的结束位置
    private int endPosition;

    /**
     * Lucene 7.6 Tokenizer适配器类构造函数
     */
    public IkTokenizer() {
        this(false);
    }

    IkTokenizer(boolean useSmart) {
        super();
        init(useSmart);
    }

    public IkTokenizer(AttributeFactory factory) {
        this(factory, false);
    }

    IkTokenizer(AttributeFactory factory, boolean useSmart) {
        super(factory);
        init(useSmart);
    }

    private void init(boolean useSmart) {
        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
        _IKImplement = new IKSegmenter(input, useSmart);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.TokenStream#incrementToken()
     */
    @Override
    public boolean incrementToken() throws IOException {
        // 清除所有的词元属性
        clearAttributes();
        Lexeme nextLexeme = _IKImplement.next();
        if (nextLexeme != null) {
            // 将Lexeme转成Attributes
            // 设置词元文本
            termAtt.append(nextLexeme.getLexemeText());
            // 设置词元长度
            termAtt.setLength(nextLexeme.getLength());
            // 设置词元位移
            offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
            // 记录分词的最后位置
            endPosition = nextLexeme.getEndPosition();
            // 记录词元分类
            typeAtt.setType(nextLexeme.getLexemeTypeString());
            // 返会true告知还有下个词元
            return true;
        }
        // 返会false告知词元输出完毕
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.Tokenizer#reset(java.io.Reader)
     */
    @Override
    public void reset() throws IOException {
        super.reset();
        _IKImplement.reset(input);
    }

    @Override
    public final void end() {
        // set final offset
        int finalOffset = correctOffset(this.endPosition);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

}