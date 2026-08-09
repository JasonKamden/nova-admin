package org.dromara.nova.message.support;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * 富文本入库前清洗 script/iframe/event handler/javascript URL。
 */
@Component
public class MessageContentSanitizer {
    private final Safelist safelist = Safelist.relaxed().addTags("table", "thead", "tbody", "tr", "th", "td").addAttributes("a", "target", "rel").addProtocols("a", "href", "http", "https", "mailto");

    /**
     * 使用 HTML 白名单清洗消息富文本，阻止危险脚本和属性。
     *
     * @param html 待清洗的富文本 HTML
     * @return 方法处理结果。
     */
    public String clean(String html) {
        return Jsoup.clean(html, safelist);
    }
}
