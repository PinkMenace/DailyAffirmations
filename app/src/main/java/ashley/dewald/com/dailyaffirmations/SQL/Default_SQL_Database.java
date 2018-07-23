package ashley.dewald.com.dailyaffirmations.SQL;

import java.util.ArrayList;
import java.util.List;

import ashley.dewald.com.dailyaffirmations.Affrimation.Affirmation;
import ashley.dewald.com.dailyaffirmations.DataCollections.AffirmationData;

public class Default_SQL_Database {
    private List<AffirmationData> data;

    public List<AffirmationData> getData() { return data; }

    public Default_SQL_Database() {
        data = new ArrayList<>();

        data.add(Love());
        data.add(Productivity());
        data.add(Leadership());
        data.add(SelfEsteem());
        data.add(WeightLoss());
    }

    public AffirmationData Love() {
        AffirmationData love = new AffirmationData("Love");

        love.add(new Affirmation("Smile"));
        love.add(new Affirmation("Don not forget to say \'hi\'"));
        love.add(new Affirmation("Flit"));
        love.add(new Affirmation("Do not use the word \'I\'."));
        love.add(new Affirmation("There are 7-billion people on this planet, do be hung up on one?"));

        return love;
    }

    private AffirmationData Productivity() {
        AffirmationData productivity = new AffirmationData("Productivity");

        productivity.add(new Affirmation("I am disciplined and productive in everything that I do."));
        productivity.add(new Affirmation("I am breaking old habits and creating new successful ones."));
        productivity.add(new Affirmation("I become more productive every single day."));
        productivity.add(new Affirmation("I have unwavering discipline and because of this I will succeed."));
        productivity.add(new Affirmation("I always win because I am willing to work harder than anyone else."));
        productivity.add(new Affirmation("I will die before I give up."));
        productivity.add(new Affirmation("Time is the most valuable resource, therefore I spend it wisely."));
        productivity.add(new Affirmation("I am the definition of sexy."));

        return productivity;
    }

    private AffirmationData Leadership() {
        AffirmationData leadership = new AffirmationData("Leadership");

        leadership.add(new Affirmation("I think, act and communicate like a leader."));
        leadership.add(new Affirmation("I am an inspirational leader."));
        leadership.add(new Affirmation("I am an effective communicator."));
        leadership.add(new Affirmation("I am a role-model for others."));
        leadership.add(new Affirmation("I inspire others to be their best self."));
        leadership.add(new Affirmation("I lead by example."));

        return leadership;
    }

    private AffirmationData SelfEsteem() {
        AffirmationData self_esteem = new AffirmationData("Self Esteem");

        self_esteem.add(new Affirmation("Mistakes are a stepping stone to success. They are the path I must tread to achieve my dreams."));
        self_esteem.add(new Affirmation("I will continue to learn and grow."));
        self_esteem.add(new Affirmation("Don't compare yourself to others."));
        self_esteem.add(new Affirmation("Positive self talk."));
        self_esteem.add(new Affirmation("Focus on the things you can change."));
        self_esteem.add(new Affirmation("Do the things you enjoy."));
        self_esteem.add(new Affirmation("Celebrate the small stuff."));
        self_esteem.add(new Affirmation("Surround yourself with supportive people."));

        return self_esteem;
    }

    private AffirmationData WeightLoss() {
        AffirmationData weight_loss = new AffirmationData("Weight Loss");

        weight_loss.add(new Affirmation("I am losing weight."));
        weight_loss.add(new Affirmation("I am slim and fit"));
        weight_loss.add(new Affirmation("I always take care of my body"));
        weight_loss.add(new Affirmation("I am motivated to lose weight and become healthy"));
        weight_loss.add(new Affirmation("I am dedicated to following my weight loss plan"));
        weight_loss.add(new Affirmation("I am disciplined in my eating habits"));
        weight_loss.add(new Affirmation("I am strong in mind and body"));
        weight_loss.add(new Affirmation("I am completely focused on losing weight"));
        weight_loss.add(new Affirmation("I only eat healthy food"));
        weight_loss.add(new Affirmation("I am focused on providing proper nutrition to my body"));

        return weight_loss;
    }
}