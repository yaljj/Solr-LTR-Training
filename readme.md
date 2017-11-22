# Solr-LTR-Training<br>
> ## Introduction
  >项目用于为Apache Solr（7.10）训练排序学习模型。项目基于特定格式的原始数据生成用于排序学习训练的数据集后，利用ranklib对数据集进行训练生成模型参数文件，并将ranklib的模型格式转换为solr的模型格式。
  目前支持的模型为[org.apache.solr.ltr.model.MultipleAdditiveTreesModel](https://lucene.apache.org/solr/7_0_0//solr-ltr/org/apache/solr/ltr/model/MultipleAdditiveTreesModel.html)。
  项目自带的原始数据源于https://www.banggood.com 一个星期内的搜索记录数据和近一个月的商品特征数据。<br>
  
> ## Quick Start
  >下面将详细描述在现有数据[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)的情况下，
  进行lambdaMART模型训练和solr-ltr配置的具体流程和操作。<br> 
>###配置商品特征
  >特征配置文件为[Solr-LTR-Training/conf/FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet),文件为json格式，用以定义特征。其中"str_prop"用于设置字符串型的属性名，“value_prop”用于设置设置数值型且不作为ltr特征的属性名。
  上述二者仅用于作为document的field上传到solr。而"rank_feature"则是用于ltr计算的特征属性。上述例子中，特征包含：<br>
  >>BM25:关联性因子，solr中默认的原始得分为org.apache.solr.ltr.feature.OriginalScoreFeature。（项目中的关联度计算为BM25,若采取其他策略需要BM25的相关的代码）<br>
  >>review：评论数<br>
