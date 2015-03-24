package moe.yuna.palinuridae.utils;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 列表分页。包含list属性。
 *
 * @author liufang
 *
 */
public class Pagination  implements java.io.Serializable {

    public Pagination() {
    }

    /**
     * 构造器
     *
     * @param pageNo
     *            页码
     * @param pageSize
     *            每页几条数据
     * @param totalCount
     *            总共几条数据
     */
    public Pagination(int pageNo, int pageSize, int totalCount) {
        setTotalCount(totalCount);
        setPageSize(pageSize);
        setPageNo(pageNo);
        adjustPageNo();
        list = new ArrayList();
    }

    /**
     * 构造器
     *
     * @param pageNo
     *            页码
     * @param pageSize
     *            每页几条数据
     * @param totalCount
     *            总共几条数据
     * @param list
     *            分页内容
     */
    public Pagination(int pageNo, int pageSize, int totalCount, List<?> list) {
        setTotalCount(totalCount);
        setPageSize(pageSize);
        setPageNo(pageNo);
        adjustPageNo();
        this.list = list;
    }
    private static final long serialVersionUID = 1L;
    public static final int DEF_COUNT = 20;

    /**
     * 检查页码 checkPageNo
     *
     * @param pageNo
     * @return if pageNo==null or pageNo<1 then return 1 else return pageNo
     */
    public static int cpn(Integer pageNo) {
        return (pageNo == null || pageNo < 1) ? 1 : pageNo;
    }

    /**
     * 构造器
     *
     * @param pageNo
     *            页码
     * @param pageSize
     *            每页几条数据
     * @param totalCount
     *            总共几条数据
     */
    /**
     * 调整页码，使不超过最大页数
     */
    public void adjustPageNo() {
        if (pageNo == 1) {
            return;
        }
        int tp = getTotalPage();
        if (pageNo > tp) {
            pageNo = tp;
        }
    }

    /**
     * 获得页码
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 每页几条数据
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 总共几条数据
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 总共几页
     */
    public int getTotalPage() {
        int totalPage = totalCount / pageSize;
        if (totalPage == 0 || totalCount % pageSize != 0) {
            totalPage++;
        }
        return totalPage;
    }

    /**
     * 是否第一页
     */
    public boolean isFirstPage() {
        return pageNo <= 1;
    }

    /**
     * 是否最后一页
     */
    public boolean isLastPage() {
        return pageNo >= getTotalPage();
    }

    /**
     * 下一页页码
     */
    public int getNextPage() {
        if (isLastPage()) {
            return pageNo;
        } else {
            return pageNo + 1;
        }
    }

    /**
     * 上一页页码
     */
    public int getPrePage() {
        if (isFirstPage()) {
            return pageNo;
        } else {
            return pageNo - 1;
        }
    }

    protected int totalCount = 0;
    protected int pageSize = 20;
    protected int pageNo = 1;

    /**
     * if totalCount<0 then totalCount=0
     *
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        if (totalCount < 0) {
            this.totalCount = 0;
        } else {
            this.totalCount = totalCount;
        }
    }

    /**
     * if pageSize< 1 then pageSize=DEF_COUNT
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            this.pageSize = DEF_COUNT;
        } else {
            this.pageSize = pageSize;
        }
    }

    /**
     * if pageNo < 1 then pageNo=1
     *
     * @param pageNo
     */
    public void setPageNo(int pageNo) {
        if (pageNo < 1) {
            this.pageNo = 1;
        } else {
            this.pageNo = pageNo;
        }
    }

    /**
     * 第一条数据位置
     *
     * @return
     */
    public int getFirstResult() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 当前页的数据
     */
    private List<?> list;

    /**
     * 获得分页内容
     *
     * @return
     */
    public List<?> getList() {
        return list;
    }

    /**
     * 设置分页内容
     *
     * @param list
     */
    @SuppressWarnings("unchecked")
    public void setList(List list) {
        this.list = list;
    }

    /**
     * 当前页的数据
     */
    private Map<?,?> map;

    /**
     * 获得分页内容
     *
     * @return
     */
    public Map<?,?> getMap() {
        return map;
    }

    /**
     * 设置分页内容
     */
    @SuppressWarnings("unchecked")
    public void setMap(Map map) {
        this.map = map;
    }

    public static int formatFirst(int pageNo, int pageSize){
        return (pageNo-1)*pageSize;
    }
    public static int formatCount(int pageNo, int pageSize){
        return pageSize;
    }

    public static int formatInputPageNo(Integer pageNo){
        return pageNo == null||pageNo<=0||pageNo>1000?1:pageNo;
    }
    public static int formatInputPageSize(Integer pageSize){
        return pageSize == null||pageSize<=0||pageSize>100?20:pageSize;
    }
    @Override
    public String toString() {
        return "Pagination{" + "totalCount=" + totalCount + ", pageSize=" + pageSize + ", pageNo=" + pageNo + ", list=" + list + ", map=" + map + '}';
    }

}
