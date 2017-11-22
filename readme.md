##Solr-LTR-Training
本项目的目的是基于ranklib工具包，为solr（7.10）训练排序学习模型。
目前支持的模型为org.apache.solr.ltr.model.MultipleAdditiveTreesModel 链接：https://lucene.apache.org/solr/7_0_0//solr-ltr/org/apache/solr/ltr/model/MultipleAdditiveTreesModel.html
，项目原始数据来源于英文电商网站的某一时段内的记录数据，包含商品特征数据和搜索词-商品对(pair)的特征数据（该特征可计算搜索词对商品的转化率）。<br>