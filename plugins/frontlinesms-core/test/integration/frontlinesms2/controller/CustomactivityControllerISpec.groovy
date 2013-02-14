package frontlinesms2.controller

import frontlinesms2.*

class CustomactivityControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller = new CustomactivityController()

	def "can create a new custom activity"(){grails.plugin.spock.IntegrationSpec
		given:
			controller.params.jsonToSubmit = """[{"stepId":"","stepType":"join", "group":"5"}, {"stepId":"","stepType":"leave", "group":"5" }]"""
			controller.params.name = "test save"
			controller.params.keywords = "test, custom"
			controller.params.sorting = "enabled"
		when:
			controller.save()
		then:
			def activity = CustomActivity.findByName("test save")
			activity.keywords*.value.containsAll(["TEST", "CUSTOM"])
			activity.steps.size() == 2
	}
	
	def "can edit an existing custom activity"() {
		given:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"1"))
			def leaveStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"2"))

			def a = new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)

			controller.params.name = "just edited"
			controller.params.keywords = "new, just, yeah"
			controller.params.sorting = "enabled"
			controller.params.ownerId = a.id
			controller.params.jsonToSubmit = """[{"stepId":"","stepType":"join", "group":"5"}]""" 
		when:
			controller.save()
		then:
			def activity = CustomActivity.findByName("just edited")
			activity.keywords*.value.containsAll(["NEW", "JUST", "YEAH"])
			activity.steps.size() == 1
	}

	def "only messages that have been triggered by the step should be displayed when viewing a step"() {
		given:
			controller = new MessageController()
			def group = Group.build()
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"${group.id}"))
			def leaveStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"${group.id}"))
			def replyStep = new ReplyActionStep().addToStepProperties(new StepProperty(key:"autoreplyText", value:"sending this message"))

			def a = new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToSteps(replyStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)
			def message = Fmessage.build(text:'steppilize this')
			a.processKeyword(message, null)
			controller.params.ownerId = a.id
			controller.params.starred = false
			controller.params.stepId = joinStep.id
		when:
			controller.activity()
		then:
			controller.modelAndView.model.messageInstanceList.size() == 1
		when:
			controller.params.stepId = replyStep.id
			controller.params.ownerId = a.id
			controller.activity()
		then:
			controller.modelAndView.model.messageInstanceList.size() == 2
	}
}
